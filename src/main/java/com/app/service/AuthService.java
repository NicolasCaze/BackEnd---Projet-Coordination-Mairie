package com.app.service;

import com.app.dto.AuthResponse;
import com.app.dto.RegisterRequest;
import com.app.dto.UserDTO;
import com.app.entity.TokenBlacklist;
import com.app.entity.User;
import com.app.exception.AuthenticationException;
import com.app.repository.TokenBlacklistRepository;
import com.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public AuthResponse authenticate(String email, String password) {
        try {
            // Récupérer l'utilisateur
            User user = userService.findByEmail(email);
            
            // Vérifier que l'utilisateur n'est pas sous tutelle
            if (user.getIs_tutored() != null && user.getIs_tutored()) {
                throw new AuthenticationException("Accès refusé : Les comptes sous tutelle ne peuvent pas se connecter");
            }
            
            // Vérifier que l'utilisateur est actif
            if (user.getStatut() != User.Statut.ACTIF) {
                throw new AuthenticationException("Compte inactif. Veuillez contacter l'administrateur.");
            }
            
            // Valider le mot de passe
            if (!userService.validatePassword(password, user.getMot_de_passe())) {
                throw new AuthenticationException("Mot de passe incorrect");
            }
            
            // Générer les tokens
            String token = jwtUtil.generateToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);
            
            // Construire la réponse
            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtUtil.getExpiration())
                    .user(UserDTO.fromEntity(user))
                    .build();
                    
        } catch (AuthenticationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new AuthenticationException("Échec de l'authentification : " + e.getMessage());
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            // Valider que c'est bien un refresh token
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw new AuthenticationException("Token de refresh invalide");
            }
            
            // Extraire l'email du token
            String email = jwtUtil.extractUsername(refreshToken);
            
            // Vérifier que l'utilisateur existe et est actif
            User user = userService.findByEmail(email);
            if (user.getStatut() != User.Statut.ACTIF) {
                throw new AuthenticationException("Compte inactif");
            }
            
            // Générer nouveaux tokens
            String newToken = jwtUtil.generateToken(email);
            String newRefreshToken = jwtUtil.generateRefreshToken(email);
            
            return AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .expiresIn(jwtUtil.getExpiration())
                    .user(UserDTO.fromEntity(user))
                    .build();
                    
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("Échec du rafraîchissement du token : " + e.getMessage());
        }
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        try {
            if (userService.findAll().stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(request.getEmail()))) {
                throw new AuthenticationException("Email déjà utilisé");
            }
            
            String hashedPassword = userService.hashPassword(request.getMot_de_passe());
            
            User user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .mot_de_passe(hashedPassword)
                    .telephone(request.getTelephone())
                    .is_resident(request.getIs_resident() != null ? request.getIs_resident() : false)
                    .role(User.Role.USER)
                    .statut(User.Statut.ACTIF)
                    .build();
            
            User savedUser = userService.create(user);
            
            String token = jwtUtil.generateToken(savedUser.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());
            
            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtUtil.getExpiration())
                    .user(UserDTO.fromEntity(savedUser))
                    .build();
                    
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("Échec de l'inscription : " + e.getMessage());
        }
    }

    @Transactional
    public void logout(String token) {
        try {
            LocalDateTime expiresAt = LocalDateTime.now()
                    .plusSeconds(jwtUtil.getExpiration());
            
            TokenBlacklist blacklistedToken = TokenBlacklist.builder()
                    .token(token)
                    .expires_at(expiresAt)
                    .build();
            
            tokenBlacklistRepository.save(blacklistedToken);
            
        } catch (Exception e) {
            throw new AuthenticationException("Échec de la déconnexion : " + e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }
}
