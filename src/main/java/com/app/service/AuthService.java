package com.app.service;

import com.app.dto.AuthResponse;
import com.app.dto.UserDTO;
import com.app.entity.User;
import com.app.exception.AuthenticationException;
import com.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthResponse authenticate(String email, String password) {
        try {
            // Récupérer l'utilisateur
            User user = userService.findByEmail(email);
            
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
}
