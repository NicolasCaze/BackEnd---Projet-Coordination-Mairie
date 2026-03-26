package com.app.filter;

import com.app.entity.ImpersonationLog;
import com.app.service.ImpersonationService;
import com.app.service.UserService;
import com.app.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImpersonationLoggingFilter extends OncePerRequestFilter {

    private final ImpersonationService impersonationService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = extractToken(request);
        UUID adminId = null;
        UUID impersonatedUserId = null;
        
        if (token != null && impersonationService.isImpersonationToken(token)) {
            try {
                adminId = impersonationService.getImpersonatedBy(token);
                impersonatedUserId = impersonationService.getImpersonatedUserId(token);
                
                // Logger l'action impersonnée
                logImpersonatedAction(request, adminId, impersonatedUserId);
                
            } catch (Exception e) {
                // Token invalide, continuer sans logging
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void logImpersonatedAction(HttpServletRequest request, UUID adminId, UUID impersonatedUserId) {
        try {
            String requestBody = null;
            if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod()) || "PATCH".equals(request.getMethod())) {
                // Pour les requêtes avec corps, on pourrait essayer de lire le corps
                // mais attention : le corps ne peut être lu qu'une seule fois
                requestBody = "[Request body logged separately]";
            }

            impersonationService.logImpersonationAction(
                    adminId,
                    impersonatedUserId,
                    "API_CALL",
                    request.getRequestURI(),
                    request.getMethod(),
                    getClientIpAddress(request),
                    request.getHeader("User-Agent"),
                    requestBody
            );
        } catch (Exception e) {
            // Ne pas bloquer la requête si le logging échoue
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
