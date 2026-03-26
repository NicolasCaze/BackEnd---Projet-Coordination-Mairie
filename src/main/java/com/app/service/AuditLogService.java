package com.app.service;

import com.app.entity.AuditLog;
import com.app.entity.User;
import com.app.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void log(UUID actorId, User.Role actorRole, UUID targetUserId, String action, Object payload) {
        try {
            String payloadJson = payload != null ? objectMapper.writeValueAsString(payload) : null;
            
            AuditLog auditLog = AuditLog.builder()
                    .actor_id(actorId)
                    .actor_role(actorRole)
                    .target_user_id(targetUserId)
                    .action(action)
                    .payload(payloadJson)
                    .build();
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }
}
