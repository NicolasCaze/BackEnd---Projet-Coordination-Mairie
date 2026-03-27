package com.app.controller;

import com.app.annotation.Audited;
import com.app.dto.CreateReservationRequest;
import com.app.dto.ReservationDTO;
import com.app.dto.UpdateCautionRequest;
import com.app.dto.UpdateValidationRequest;
import com.app.dto.PagedResponse;
import com.app.entity.Bien;
import com.app.entity.Delegation;
import com.app.entity.Groupe;
import com.app.entity.Reservation;
import com.app.entity.User;
import com.app.service.AuthService;
import com.app.service.BienService;
import com.app.service.DelegationPermissionService;
import com.app.service.GroupeService;
import com.app.service.ReservationService;
import com.app.service.UserGroupeService;
import com.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservations")
@Tag(name = "Réservations", description = "Gestion des réservations de biens municipaux")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final GroupeService groupeService;
    private final BienService bienService;
    private final UserGroupeService userGroupeService;
    private final DelegationPermissionService delegationPermissionService;
    
    public ReservationController(ReservationService reservationService, UserService userService, 
                           GroupeService groupeService, BienService bienService, 
                           UserGroupeService userGroupeService, DelegationPermissionService delegationPermissionService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.groupeService = groupeService;
        this.bienService = bienService;
        this.userGroupeService = userGroupeService;
        this.delegationPermissionService = delegationPermissionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        User user = userService.findById(request.getId_user());
        Bien bien = bienService.findById(request.getId_bien());
        
        Groupe groupe = null;
        if (request.getId_groupe() != null) {
            groupe = groupeService.findById(request.getId_groupe());
            
            // Vérifier que l'utilisateur est un membre actif du groupe
            if (!userGroupeService.isUserActiveMember(request.getId_user(), request.getId_groupe())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .bien(bien)
                .groupe(groupe)
                .date_debut(request.getDateDebut())
                .date_fin(request.getDateFin())
                .build();

        Reservation createdReservation = reservationService.create(reservation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationDTO.fromEntity(createdReservation));
    }

    @PatchMapping("/{id}/statut")
    @Audited(action = "PATCH_STATUT_RESERVATION")
    @Operation(summary = "Valide une réservation", description = "Change le statut de validation d'une réservation avec permissions déléguées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ReservationDTO> updateReservationValidation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateValidationRequest request,
            Authentication authentication) {
        
        delegationPermissionService.checkPermissionOrDelegatedPermission(
            authentication, 
            Delegation.Permission.UPDATE_RESERVATION_VALIDATION,
            "Vous n'avez pas la permission de valider une réservation"
        );
        
        Reservation updatedReservation = reservationService.updateEstValide(id, request.getEst_valide());
        return ResponseEntity.ok(ReservationDTO.fromEntity(updatedReservation));
    }

    @PatchMapping("/{id}/caution")
    @Audited(action = "PATCH_CAUTION_RESERVATION")
    @Operation(summary = "Modifie la caution d'une réservation", description = "Met à jour le statut de caution d'une réservation avec permissions déléguées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Caution mise à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ReservationDTO> updateReservationCaution(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCautionRequest request,
            Authentication authentication) {
        
        delegationPermissionService.checkPermissionOrDelegatedPermission(
            authentication, 
            Delegation.Permission.UPDATE_RESERVATION_CAUTION,
            "Vous n'avez pas la permission de modifier la caution d'une réservation"
        );
        
        Reservation updatedReservation = reservationService.updateEstCaution(id, request.getEst_caution());
        return ResponseEntity.ok(ReservationDTO.fromEntity(updatedReservation));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<PagedResponse<ReservationDTO>> getReservationsByUser(@PathVariable UUID userId, Pageable pageable) {
        userService.findById(userId);
        Page<Reservation> reservationPage = reservationService.findByUser(userId, pageable);
        Page<ReservationDTO> reservationDTOPage = reservationPage.map(ReservationDTO::fromEntity);
        return ResponseEntity.ok(PagedResponse.of(reservationDTOPage));
    }

    @GetMapping("/groupes/{groupeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<PagedResponse<ReservationDTO>> getReservationsByGroupe(@PathVariable UUID groupeId, Pageable pageable) {
        groupeService.findById(groupeId);
        Page<Reservation> reservationPage = reservationService.findByGroupe(groupeId, pageable);
        Page<ReservationDTO> reservationDTOPage = reservationPage.map(ReservationDTO::fromEntity);
        return ResponseEntity.ok(PagedResponse.of(reservationDTOPage));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable UUID id) {
        Reservation reservation = reservationService.findById(id);
        return ResponseEntity.ok(ReservationDTO.fromEntity(reservation));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<PagedResponse<ReservationDTO>> getAllReservations(Pageable pageable) {
        Page<Reservation> reservationPage = reservationService.findAll(pageable);
        Page<ReservationDTO> reservationDTOPage = reservationPage.map(ReservationDTO::fromEntity);
        return ResponseEntity.ok(PagedResponse.of(reservationDTOPage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable UUID id, Authentication authentication) {
        delegationPermissionService.checkPermissionOrDelegatedPermission(
            authentication, 
            Delegation.Permission.DELETE_RESERVATION,
            "Vous n'avez pas la permission de supprimer une réservation"
        );
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
