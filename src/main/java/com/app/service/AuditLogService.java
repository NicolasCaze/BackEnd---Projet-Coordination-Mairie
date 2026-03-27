package com.app.service;

import com.app.dto.AuditLogDTO;
import com.app.entity.AuditLog;
import com.app.entity.User;
import com.app.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    public Page<AuditLogDTO> getAuditLogs(UUID actorId, UUID targetUserId, String action, 
                                         LocalDateTime dateFrom, LocalDateTime dateTo, 
                                         Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogRepository.findByFilters(
                actorId, targetUserId, action, dateFrom, dateTo, pageable);
        
        return auditLogs.map(AuditLogDTO::fromEntity);
    }
    
    public Page<AuditLogDTO> getAuditLogs(UUID actorId, UUID targetUserId, String action, 
                                         LocalDateTime dateFrom, LocalDateTime dateTo, 
                                         int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        
        Page<AuditLog> auditLogs = auditLogRepository.findByFilters(
                actorId, targetUserId, action, dateFrom, dateTo, pageable);
        
        return auditLogs.map(AuditLogDTO::fromEntity);
    }
}
