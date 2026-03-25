package com.app.dto;

import com.app.entity.Delegation;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DelegationResponse {
    
    private UUID id;
    private UUID fromUserId;
    private UUID toUserId;
    private Delegation.Permission permission;
    private LocalDateTime delegatedAt;
    private LocalDateTime revokedAt;
    private Boolean active;
    
    public static DelegationResponse fromEntity(Delegation delegation) {
        return DelegationResponse.builder()
                .id(delegation.getId())
                .fromUserId(delegation.getFromUserId())
                .toUserId(delegation.getToUserId())
                .permission(delegation.getPermission())
                .delegatedAt(delegation.getDelegatedAt())
                .revokedAt(delegation.getRevokedAt())
                .active(delegation.getActive())
                .build();
    }
}
