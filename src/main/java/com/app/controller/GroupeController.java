package com.app.controller;

import com.app.dto.GroupeDTO;
import com.app.dto.RequiredDocumentResponse;
import com.app.dto.ReservationDTO;
import com.app.dto.UserGroupeDTO;
import com.app.dto.PagedResponse;
import com.app.entity.Delegation;
import com.app.entity.Groupe;
import com.app.entity.Reservation;
import com.app.entity.UserGroupe;
import com.app.service.DelegationPermissionService;
import com.app.service.DocumentRuleService;
import com.app.service.EmailService;
import com.app.service.GroupeService;
import com.app.service.ReservationService;
import com.app.service.UserGroupeService;
import com.app.service.UserService;
import com.app.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groupes")
@Tag(name = "Groupes", description = "Gestion des groupes d'utilisateurs et de leurs membres")
public class GroupeController {

    private final GroupeService groupeService;
    private final ReservationService reservationService;
    private final UserGroupeService userGroupeService;
    private final DocumentRuleService documentRuleService;
    private final DelegationPermissionService delegationPermissionService;
    private final EmailService emailService;
    private final UserService userService;

    public GroupeController(GroupeService groupeService, ReservationService reservationService, 
                         UserGroupeService userGroupeService, DocumentRuleService documentRuleService, 
                         DelegationPermissionService delegationPermissionService,
                         EmailService emailService, UserService userService) {
        this.groupeService = groupeService;
        this.reservationService = reservationService;
        this.userGroupeService = userGroupeService;
        this.documentRuleService = documentRuleService;
        this.delegationPermissionService = delegationPermissionService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping("/{id}/reservations")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<PagedResponse<ReservationDTO>> getGroupeReservations(@PathVariable UUID id, Pageable pageable) {
        groupeService.findById(id);
        Page<Reservation> reservationPage = reservationService.findByGroupe(id, pageable);
        Page<ReservationDTO> reservationDTOPage = reservationPage.map(ReservationDTO::fromEntity);
        return ResponseEntity.ok(PagedResponse.of(reservationDTOPage));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Récupérer mes groupes", description = "Retourne tous les groupes de l'utilisateur connecté")
    public ResponseEntity<List<GroupeDTO>> getMyGroupes(Authentication authentication) {
        String email = authentication.getName();
        List<Groupe> groupes = groupeService.findGroupesByUserEmail(email);
        List<GroupeDTO> groupeDTOs = groupes.stream()
                .map(GroupeDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(groupeDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    @Operation(summary = "Récupère un groupe par ID", description = "Retourne les détails d'un groupe spécifique (ADMIN/MODERATEUR)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Groupe trouvé"),
        @ApiResponse(responseCode = "404", description = "Groupe non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Groupe> getGroupeById(@PathVariable @Parameter(description = "ID du groupe") UUID id) {
        Groupe groupe = groupeService.findById(id);
        return ResponseEntity.ok(groupe);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MODERATEUR')")
    @Operation(summary = "Liste tous les groupes", description = "Récupère la liste paginée des groupes (ADMIN/MODERATEUR)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des groupes récupérée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<PagedResponse<GroupeDTO>> getAllGroupes(Pageable pageable) {
        Page<Groupe> groupePage = groupeService.findAll(pageable);
        Page<GroupeDTO> groupeDTOPage = groupePage.map(groupe -> {
            GroupeDTO dto = GroupeDTO.fromEntity(groupe);
            // Charger les membres avec leurs rôles
            List<UserGroupe> membres = userGroupeService.findMembresByGroupe(groupe.getId_groupe());
            List<GroupeDTO.MembreInfo> membresInfo = membres.stream()
                .map(ug -> GroupeDTO.MembreInfo.builder()
                    .id_user(ug.getUser().getId_user())
                    .nom(ug.getUser().getNom())
                    .prenom(ug.getUser().getPrenom())
                    .email(ug.getUser().getEmail())
                    .roleGroupe(ug.getRoleGroupe() != null ? ug.getRoleGroupe().name() : "MEMBRE")
                    .build())
                .collect(Collectors.toList());
            dto.setMembres(membresInfo);
            return dto;
        });
        return ResponseEntity.ok(PagedResponse.of(groupeDTOPage));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MODERATEUR')")
    @Operation(summary = "Crée un nouveau groupe", description = "Crée un groupe d'utilisateurs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Groupe créé avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Groupe> createGroupe(@RequestBody Groupe groupe, Authentication authentication) {
        Groupe createdGroupe = groupeService.create(groupe);
        return ResponseEntity.status(201).body(createdGroupe);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<Groupe> updateGroupe(@PathVariable UUID id, @RequestBody Groupe groupe) {
        Groupe updatedGroupe = groupeService.update(id, groupe);
        return ResponseEntity.ok(updatedGroupe);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MODERATEUR')")
    @Operation(summary = "Supprime un groupe", description = "Supprime un groupe (sauf CONSEIL_MUNICIPAL)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Groupe supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Groupe non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteGroupe(@PathVariable @Parameter(description = "ID du groupe") UUID id, Authentication authentication) {
        groupeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/membres/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<UserGroupeDTO> addMembre(@PathVariable UUID id, @PathVariable UUID userId) {
        UserGroupe userGroupe = userGroupeService.addMembre(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserGroupeDTO.fromEntity(userGroupe));
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<UserGroupeDTO> addMembreWithRole(
            @PathVariable UUID id, 
            @RequestBody AddMembreRequest request) {
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            UUID userId = UUID.fromString(request.getUserId());
            UserGroupe.RoleGroupe roleGroupe = request.getRoleGroupe() != null 
                ? UserGroupe.RoleGroupe.valueOf(request.getRoleGroupe()) 
                : UserGroupe.RoleGroupe.MEMBRE;
            UserGroupe userGroupe = userGroupeService.addMembreWithRole(id, userId, roleGroupe);
            
            // Envoyer un email si c'est un admin de groupe
            if (roleGroupe == UserGroupe.RoleGroupe.ADMIN) {
                try {
                    Groupe groupe = groupeService.findById(id);
                    User adminUser = userService.findById(userId);
                    
                    // Debug: vérifier le code d'invitation
                    System.out.println("DEBUG - Code d'invitation du groupe: " + groupe.getCodeInvitation());
                    System.out.println("DEBUG - Nom du groupe: " + groupe.getNom());
                    System.out.println("DEBUG - Email admin: " + adminUser.getEmail());
                    
                    emailService.sendGroupeInvitationToAdmin(groupe, adminUser);
                    System.out.println("Email envoyé avec succès à " + adminUser.getEmail());
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(UserGroupeDTO.fromEntity(userGroupe));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public static class AddMembreRequest {
        private String userId;
        private String roleGroupe;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getRoleGroupe() { return roleGroupe; }
        public void setRoleGroupe(String roleGroupe) { this.roleGroupe = roleGroupe; }
    }

    @DeleteMapping("/{id}/membres/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<Void> removeMembre(@PathVariable UUID id, @PathVariable UUID userId) {
        userGroupeService.removeMembre(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/membres/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<UserGroupeDTO> updateMembreStatus(
            @PathVariable UUID id, 
            @PathVariable UUID userId,
            @RequestBody UserGroupeDTO userGroupeDTO) {
        UserGroupe userGroupe = userGroupeService.updateStatut(id, userId, userGroupeDTO.getStatus());
        return ResponseEntity.ok(UserGroupeDTO.fromEntity(userGroupe));
    }

    @GetMapping("/{id}/membres")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<UserGroupeDTO>> getGroupeMembres(@PathVariable UUID id, Pageable pageable) {
        System.out.println("DEBUG - Récupération des membres du groupe: " + id);
        Page<UserGroupe> membrePage = userGroupeService.findMembresByGroupe(id, pageable);
        System.out.println("DEBUG - Nombre de membres trouvés: " + membrePage.getTotalElements());
        
        Page<UserGroupeDTO> membreDTOPage = membrePage.map(userGroupe -> {
            UserGroupeDTO dto = UserGroupeDTO.fromEntity(userGroupe);
            System.out.println("DEBUG - Membre: " + dto.getPrenom() + " " + dto.getNom() + " - Role: " + dto.getRoleGroupe());
            return dto;
        });
        
        return ResponseEntity.ok(PagedResponse.of(membreDTOPage));
    }

    @GetMapping("/{id}/required-documents")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<RequiredDocumentResponse> getRequiredDocuments(@PathVariable UUID id) {
        RequiredDocumentResponse response = documentRuleService.getRequiredDocumentsForGroup(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join-by-code")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Rejoindre un groupe via code d'invitation", description = "Permet à un utilisateur de rejoindre un groupe en utilisant son code d'invitation")
    public ResponseEntity<UserGroupeDTO> joinGroupByCode(
            @RequestBody JoinGroupRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User currentUser = userService.findByEmail(email);
            
            Groupe groupe = groupeService.findByCodeInvitation(request.getCodeInvitation());
            if (groupe == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Vérifier si l'utilisateur est déjà membre
            if (userGroupeService.isUserMember(currentUser.getId_user(), groupe.getId_groupe())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            UserGroupe userGroupe = userGroupeService.addMembreWithRole(
                groupe.getId_groupe(), 
                currentUser.getId_user(), 
                UserGroupe.RoleGroupe.MEMBRE
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(UserGroupeDTO.fromEntity(userGroupe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public static class JoinGroupRequest {
        private String codeInvitation;

        public String getCodeInvitation() { return codeInvitation; }
        public void setCodeInvitation(String codeInvitation) { this.codeInvitation = codeInvitation; }
    }
}
