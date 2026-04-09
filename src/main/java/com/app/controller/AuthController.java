package com.app.controller;

import com.app.dto.AuthRequest;
import com.app.dto.AuthResponse;
import com.app.dto.RegisterRequest;
import com.app.dto.UserDTO;
import com.app.entity.User;
import com.app.exception.AuthenticationException;
import com.app.service.AuthService;
import com.app.service.UserService;
import com.app.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Gestion de l'authentification et des tokens JWT")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
        @ApiResponse(responseCode = "403", description = "Compte sous tutelle ou inactif")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            AuthResponse response = authService.authenticate(
                    authRequest.getEmail(),
                    authRequest.getMot_de_passe()
            );
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            if (e.getMessage().contains("comptes sous tutelle")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription utilisateur", description = "Crée un nouveau compte utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Compte créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou email déjà utilisé")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.isEmpty() || authHeader.equals("Bearer null") || authHeader.equals("Bearer undefined")) {
                return ResponseEntity.ok().build();
            }
            String token = authHeader.replace("Bearer ", "");
            if (token != null && !token.isEmpty() && !token.equals("null") && !token.equals("undefined")) {
                authService.logout(token);
            }
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody String refreshToken) {
        try {
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Récupérer l'utilisateur connecté", description = "Retourne les informations de l'utilisateur actuellement connecté")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            // Vérifier si le token est blacklisté
            if (authService.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Extraire l'email du token
            String email = jwtUtil.extractUsername(token);
            
            // Récupérer l'utilisateur
            User user = userService.findByEmail(email);
            
            // Vérifier que l'utilisateur est actif
            if (user.getStatut() != User.Statut.ACTIF) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            return ResponseEntity.ok(UserDTO.fromEntity(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
