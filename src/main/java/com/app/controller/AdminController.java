package com.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import com.app.dto.DelegationRequest;
import com.app.dto.DelegationResponse;
import com.app.dto.AuditLogDTO;
import com.app.dto.PagedResponse;
import com.app.entity.Delegation;
import com.app.entity.User;
import com.app.service.DelegationService;
import com.app.service.ImpersonationService;
import com.app.service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Fonctionnalités d'administration: délégations, impersonation, audit logs")
public class AdminController {

    private final DelegationService delegationService;
    private final ImpersonationService impersonationService;
    private final AuditLogService auditLogService;

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
    public ResponseEntity<PagedResponse<DelegationResponse>> getMyDelegations(Authentication authentication, Pageable pageable) {
        User currentUser = (User) authentication.getPrincipal();
        
        Page<Delegation> delegationPage = delegationService.getDelegationsReceivedByUser(currentUser.getId_user(), pageable);
        Page<DelegationResponse> delegationResponsePage = delegationPage.map(DelegationResponse::fromEntity);
        
        return ResponseEntity.ok(PagedResponse.of(delegationResponsePage));
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

    @PostMapping("/impersonate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Impersonate un utilisateur", description = "Permet à un admin de se connecter en tant qu'un autre utilisateur (ADMIN uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token d'impersonation généré"),
        @ApiResponse(responseCode = "400", description = "Utilisateur invalide"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Map<String, Object>> impersonateUser(
            @PathVariable UUID userId,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            String impersonationToken = impersonationService.generateImpersonationToken(
                    currentUser.getId_user(), userId);
            
            // Logger l'action d'impersonation
            impersonationService.logImpersonationAction(
                    currentUser.getId_user(),
                    userId,
                    "IMPERSONATION_START",
                    "/admin/impersonate/" + userId,
                    "POST",
                    null, // IP address sera ajoutée par un filtre
                    null, // User agent sera ajouté par un filtre
                    null
            );
            
            return ResponseEntity.ok(Map.of(
                    "token", impersonationToken,
                    "expiresIn", 1800, // 30 minutes
                    "impersonatedUserId", userId.toString(),
                    "message", "Impersonation démarrée avec succès"
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consulte les logs d'audit", description = "Récupère les logs d'audit avec filtres optionnels (ADMIN uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logs d'audit récupérés"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<PagedResponse<AuditLogDTO>> getAuditLogs(
            @RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) UUID targetUserId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            Pageable pageable) {
        
        Page<AuditLogDTO> auditLogPage = auditLogService.getAuditLogs(
                actorId, targetUserId, action, dateFrom, dateTo, pageable);
        
        return ResponseEntity.ok(PagedResponse.of(auditLogPage));
    }
}
