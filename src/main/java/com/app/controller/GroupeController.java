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
import com.app.service.GroupeService;
import com.app.service.ReservationService;
import com.app.service.UserGroupeService;
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

    public GroupeController(GroupeService groupeService, ReservationService reservationService, 
                         UserGroupeService userGroupeService, DocumentRuleService documentRuleService, 
                         DelegationPermissionService delegationPermissionService) {
        this.groupeService = groupeService;
        this.reservationService = reservationService;
        this.userGroupeService = userGroupeService;
        this.documentRuleService = documentRuleService;
        this.delegationPermissionService = delegationPermissionService;
    }

    @GetMapping("/{id}/reservations")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<PagedResponse<ReservationDTO>> getGroupeReservations(@PathVariable UUID id, Pageable pageable) {
        groupeService.findById(id);
        Page<Reservation> reservationPage = reservationService.findByGroupe(id, pageable);
        Page<ReservationDTO> reservationDTOPage = reservationPage.map(ReservationDTO::fromEntity);
        return ResponseEntity.ok(PagedResponse.of(reservationDTOPage));
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    @Operation(summary = "Liste tous les groupes", description = "Récupère la liste paginée des groupes (ADMIN/MODERATEUR)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des groupes récupérée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<PagedResponse<Groupe>> getAllGroupes(Pageable pageable) {
        Page<Groupe> groupePage = groupeService.findAll(pageable);
        return ResponseEntity.ok(PagedResponse.of(groupePage));
    }

    @PostMapping
    @Operation(summary = "Crée un nouveau groupe", description = "Crée un groupe d'utilisateurs avec permissions déléguées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Groupe créé avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Groupe> createGroupe(@RequestBody Groupe groupe, Authentication authentication) {
        delegationPermissionService.checkPermissionOrDelegatedPermission(
            authentication, 
            Delegation.Permission.CREATE_GROUPE,
            "Vous n'avez pas la permission de créer un groupe"
        );
        Groupe createdGroupe = groupeService.create(groupe);
        return ResponseEntity.status(201).body(createdGroupe);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<Groupe> updateGroupe(@PathVariable UUID id, @RequestBody Groupe groupe) {
        Groupe updatedGroupe = groupeService.update(id, groupe);
        return ResponseEntity.ok(updatedGroupe);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un groupe", description = "Supprime un groupe (sauf CONSEIL_MUNICIPAL) avec permissions déléguées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Groupe supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Groupe non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteGroupe(@PathVariable @Parameter(description = "ID du groupe") UUID id, Authentication authentication) {
        delegationPermissionService.checkPermissionOrDelegatedPermission(
            authentication, 
            Delegation.Permission.DELETE_GROUPE,
            "Vous n'avez pas la permission de supprimer un groupe"
        );
        groupeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/membres/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<UserGroupeDTO> addMembre(@PathVariable UUID id, @PathVariable UUID userId) {
        UserGroupe userGroupe = userGroupeService.addMembre(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserGroupeDTO.fromEntity(userGroupe));
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<PagedResponse<UserGroupeDTO>> getGroupeMembres(@PathVariable UUID id, Pageable pageable) {
        Page<UserGroupe> membrePage = userGroupeService.findMembresByGroupe(id, pageable);
        Page<UserGroupeDTO> membreDTOPage = membrePage.map(UserGroupeDTO::fromEntity);
        return ResponseEntity.ok(PagedResponse.of(membreDTOPage));
    }

    @GetMapping("/{id}/required-documents")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<RequiredDocumentResponse> getRequiredDocuments(@PathVariable UUID id) {
        RequiredDocumentResponse response = documentRuleService.getRequiredDocumentsForGroup(id);
        return ResponseEntity.ok(response);
    }
}
