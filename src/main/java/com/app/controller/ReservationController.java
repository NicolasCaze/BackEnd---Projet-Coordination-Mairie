package com.app.controller;

import com.app.annotation.Audited;
import com.app.dto.CreateReservationRequest;
import com.app.dto.ReservationDTO;
import com.app.dto.UpdateCautionRequest;
import com.app.dto.UpdateValidationRequest;
import com.app.entity.Bien;
import com.app.entity.Delegation;
import com.app.entity.Groupe;
import com.app.entity.Reservation;
import com.app.entity.User;
import com.app.entity.UserGroupe;
import com.app.entity.UserGroupeId;
import com.app.service.AuthService;
import com.app.service.BienService;
import com.app.service.DelegationPermissionService;
import com.app.service.GroupeService;
import com.app.service.ReservationService;
import com.app.service.UserGroupeService;
import com.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.app.service.GroupeService;
import com.app.service.ReservationService;
import com.app.service.UserGroupeService;
import com.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final GroupeService groupeService;
    private final BienService bienService;
    private final UserGroupeService userGroupeService;
    private final DelegationPermissionService delegationPermissionService;

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
    public ResponseEntity<List<ReservationDTO>> getReservationsByUser(@PathVariable UUID userId) {
        userService.findById(userId);
        List<Reservation> reservations = reservationService.findByUser(userId);
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservationDTOs);
    }

    @GetMapping("/groupes/{groupeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<List<ReservationDTO>> getReservationsByGroupe(@PathVariable UUID groupeId) {
        groupeService.findById(groupeId);
        List<Reservation> reservations = reservationService.findByGroupe(groupeId);
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservationDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable UUID id) {
        Reservation reservation = reservationService.findById(id);
        return ResponseEntity.ok(ReservationDTO.fromEntity(reservation));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<Reservation> reservations = reservationService.findAll();
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservationDTOs);
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
