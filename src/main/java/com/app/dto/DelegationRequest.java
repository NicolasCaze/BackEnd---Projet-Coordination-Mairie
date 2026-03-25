package com.app.dto;

import com.app.entity.Delegation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DelegationRequest {
    
    @NotNull(message = "L'ID de l'utilisateur source est requis")
    private UUID fromUserId;
    
    @NotNull(message = "L'ID de l'utilisateur cible est requis")
    private UUID toUserId;
    
    @NotNull(message = "La permission est requise")
    private Delegation.Permission permission;
}
