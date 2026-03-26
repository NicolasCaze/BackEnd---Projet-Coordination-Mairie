package com.app.service;

import com.app.entity.ImpersonationLog;
import com.app.entity.User;
import com.app.repository.ImpersonationLogRepository;
import com.app.repository.UserRepository;
import com.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ImpersonationService {

    private final UserRepository userRepository;
    private final ImpersonationLogRepository impersonationLogRepository;
    private final JwtUtil jwtUtil;

    private static final long IMPERSONATION_TOKEN_EXPIRATION = 30 * 60; // 30 minutes en secondes

    public String generateImpersonationToken(UUID adminId, UUID targetUserId) {
        // Vérifier que l'admin existe
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin non trouvé: " + adminId));

        // Vérifier que l'utilisateur cible existe
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur cible non trouvé: " + targetUserId));

        // Vérifier que l'admin est bien un admin
        if (admin.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Seul un admin peut initier une impersonation");
        }

        // Vérifier que l'utilisateur cible est bien sous tutelle
        if (targetUser.getIs_tutored() == null || !targetUser.getIs_tutored()) {
            throw new RuntimeException("L'impersonation n'est possible que pour les comptes sous tutelle");
        }

        // Générer le token temporaire avec claim impersonatedBy
        Map<String, Object> claims = new HashMap<>();
        claims.put("impersonatedBy", adminId.toString());
        claims.put("type", "impersonation");
        claims.put("impersonatedUserId", targetUserId.toString());

        return createImpersonationToken(claims, targetUser.getEmail(), IMPERSONATION_TOKEN_EXPIRATION * 1000);
    }

    private String createImpersonationToken(Map<String, Object> claims, String subject, Long validity) {
        return jwtUtil.createCustomToken(claims, subject, validity);
    }

    public void logImpersonationAction(UUID adminId, UUID impersonatedUserId, String action, 
                                     String endpoint, String httpMethod, String ipAddress, 
                                     String userAgent, String requestBody) {
        ImpersonationLog log = ImpersonationLog.builder()
                .adminId(adminId)
                .impersonatedUserId(impersonatedUserId)
                .action(action)
                .endpoint(endpoint)
                .httpMethod(httpMethod)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .requestBody(requestBody)
                .build();

        impersonationLogRepository.save(log);
    }

    public boolean isImpersonationToken(String token) {
        try {
            String type = jwtUtil.extractClaim(token, claims -> claims.get("type", String.class));
            return "impersonation".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    public UUID getImpersonatedBy(String token) {
        try {
            String impersonatedBy = jwtUtil.extractClaim(token, claims -> claims.get("impersonatedBy", String.class));
            return UUID.fromString(impersonatedBy);
        } catch (Exception e) {
            throw new RuntimeException("Token d'impersonation invalide");
        }
    }

    public UUID getImpersonatedUserId(String token) {
        try {
            String impersonatedUserId = jwtUtil.extractClaim(token, claims -> claims.get("impersonatedUserId", String.class));
            return UUID.fromString(impersonatedUserId);
        } catch (Exception e) {
            throw new RuntimeException("Token d'impersonation invalide");
        }
    }
}
