package com.app.aspect;

import com.app.annotation.Audited;
import com.app.entity.User;
import com.app.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    @AfterReturning(value = "@annotation(com.app.annotation.Audited)", returning = "result")
    public void auditMethod(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Audited audited = method.getAnnotation(Audited.class);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            UUID actorId = null;
            User.Role actorRole = null;
            
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof User) {
                    User user = (User) principal;
                    actorId = user.getId_user();
                    actorRole = user.getRole();
                }
            }

            UUID targetUserId = extractTargetUserId(joinPoint);
            
            Map<String, Object> payload = new HashMap<>();
            String[] paramNames = signature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();
            
            for (int i = 0; i < paramNames.length; i++) {
                if (paramValues[i] != null && !paramNames[i].equals("authentication")) {
                    payload.put(paramNames[i], paramValues[i].toString());
                }
            }

            auditLogService.log(actorId, actorRole, targetUserId, audited.action(), payload);
            
        } catch (Exception e) {
            System.err.println("Audit logging failed: " + e.getMessage());
        }
    }

    private UUID extractTargetUserId(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UUID) {
                return (UUID) arg;
            }
        }
        return null;
    }
}
