package com.app.controller;

import com.app.dto.DelegationRequest;
import com.app.dto.DelegationResponse;
import com.app.entity.Delegation;
import com.app.entity.User;
import com.app.service.DelegationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DelegationService delegationService;

    @PostMapping("/delegations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DelegationResponse> createDelegation(
            @Valid @RequestBody DelegationRequest request,
            Authentication authentication) {
        
        // Vérifier que l'utilisateur authentifié est bien le fromUserId
        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.getId_user().equals(request.getFromUserId())) {
            throw new RuntimeException("Vous ne pouvez déléguer que vos propres permissions");
        }

        Delegation delegation = delegationService.createDelegation(
                request.getFromUserId(),
                request.getToUserId(),
                request.getPermission()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DelegationResponse.fromEntity(delegation));
    }

    @GetMapping("/delegations/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DelegationResponse>> getMyDelegations(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        List<Delegation> delegations = delegationService.getDelegationsReceivedByUser(currentUser.getId_user());
        
        List<DelegationResponse> delegationResponses = delegations.stream()
                .map(DelegationResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(delegationResponses);
    }

    @DeleteMapping("/delegations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> revokeDelegation(
            @PathVariable UUID id,
            Authentication authentication) {
        
        // Vérifier que l'utilisateur est soit le créateur de la délégation, soit l'admin système
        User currentUser = (User) authentication.getPrincipal();
        
        delegationService.revokeDelegation(id);
        
        return ResponseEntity.noContent().build();
    }
}
