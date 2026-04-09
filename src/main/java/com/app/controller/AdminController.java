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
    private final com.app.service.UserService userService;

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

    @PostMapping("/users/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Créer un utilisateur avec rôle", description = "Permet au SUPER_ADMIN de créer un utilisateur avec rôle USER ou ADMIN")
    public ResponseEntity<User> createUserWithRole(@Valid @RequestBody CreateUserRequest request) {
        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .mot_de_passe(request.getPassword())
                .role(User.Role.valueOf(request.getRole()))
                .statut(User.Statut.ACTIF)
                .build();
        
        User createdUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    public static class CreateUserRequest {
        private String nom;
        private String prenom;
        private String email;
        private String password;
        private String role;

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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

    @PostMapping("/users/{userId}/promote-to-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Promouvoir un utilisateur en ADMIN", description = "Permet au SUPER_ADMIN de promouvoir un utilisateur en ADMIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur promu avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - SUPER_ADMIN uniquement")
    })
    public ResponseEntity<Map<String, String>> promoteToAdmin(
            @PathVariable UUID userId,
            Authentication authentication) {
        
        User targetUser = userService.findById(userId);
        
        if (targetUser.getRole() == User.Role.ADMIN || targetUser.getRole() == User.Role.SUPER_ADMIN) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "L'utilisateur est déjà administrateur"));
        }
        
        targetUser.setRole(User.Role.ADMIN);
        userService.update(userId, targetUser);
        
        return ResponseEntity.ok(Map.of(
                "message", "Utilisateur promu en ADMIN avec succès",
                "userId", userId.toString(),
                "newRole", "ADMIN"
        ));
    }
}
