package com.app.dto;

import com.app.entity.User;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    
    private UUID id;
    private UUID actorId;
    private User.Role actorRole;
    private UUID targetUserId;
    private String action;
    private String payload;
    private LocalDateTime timestamp;
    
    public static AuditLogDTO fromEntity(com.app.entity.AuditLog auditLog) {
        return AuditLogDTO.builder()
                .id(auditLog.getId())
                .actorId(auditLog.getActor_id())
                .actorRole(auditLog.getActor_role())
                .targetUserId(auditLog.getTarget_user_id())
                .action(auditLog.getAction())
                .payload(auditLog.getPayload())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
