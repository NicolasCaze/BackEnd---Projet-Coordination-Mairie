package com.app.controller;

import com.app.annotation.Audited;
import com.app.dto.UserDTO;
import com.app.dto.ReservationDTO;
import com.app.dto.PagedResponse;
import com.app.entity.Reservation;
import com.app.entity.User;
import com.app.entity.Delegation;
import com.app.service.DelegationPermissionService;
import com.app.service.ReservationService;
import com.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/users")
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs et de leurs réservations")
public class UserController {

    private final UserService userService;
    private final ReservationService reservationService;
    private final DelegationPermissionService delegationPermissionService;
    
    public UserController(UserService userService, ReservationService reservationService, DelegationPermissionService delegationPermissionService) {
        this.userService = userService;
        this.reservationService = reservationService;
        this.delegationPermissionService = delegationPermissionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<UserDTO>> getAllUsers(Pageable pageable) {
        Page<User> userPage = userService.findAll(pageable);
        Page<UserDTO> userDTOPage = userPage.map(UserDTO::fromEntity);
        return ResponseEntity.ok(PagedResponse.of(userDTOPage));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = User.builder()
                .nom(userDTO.getNom())
                .prenom(userDTO.getPrenom())
                .email(userDTO.getEmail())
                .mot_de_passe(userDTO.getMot_de_passe())
                .telephone(userDTO.getTelephone())
                .is_resident(userDTO.getIs_resident())
                .is_tutored(userDTO.getIs_tutored())
                .niveau_tarif(userDTO.getNiveau_tarif())
                .role(userDTO.getRole())
                .statut(userDTO.getStatut())
                .build();
        
        User createdUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserDTO.fromEntity(createdUser));
    }

    @PutMapping("/profile")
    @Operation(summary = "Mettre à jour son profil", description = "Permet à l'utilisateur connecté de mettre à jour ses informations personnelles")
    public ResponseEntity<UserDTO> updateProfile(
            @Valid @RequestBody UserDTO userDTO,
            Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        // Mettre à jour uniquement les champs autorisés (pas le role ni le statut)
        User updatedUser = userService.updateProfile(currentUser.getId_user(), userDTO);
        return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id, 
            @Valid @RequestBody UserDTO userDTO,
            @RequestHeader(value = "X-User-Id", required = false) UUID currentUserId) {
        User user = User.builder()
                .nom(userDTO.getNom())
                .prenom(userDTO.getPrenom())
                .email(userDTO.getEmail())
                .mot_de_passe(userDTO.getMot_de_passe())
                .telephone(userDTO.getTelephone())
                .is_resident(userDTO.getIs_resident())
                .is_tutored(userDTO.getIs_tutored())
                .niveau_tarif(userDTO.getNiveau_tarif())
                .role(userDTO.getRole())
                .statut(userDTO.getStatut())
                .build();
        
        User updatedUser;
        if (currentUserId != null) {
            updatedUser = userService.updateWithoutRoleChange(id, user, currentUserId);
        } else {
            updatedUser = userService.update(id, user);
        }
        
        return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
    }

    @GetMapping("/{id}/reservations")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<PagedResponse<ReservationDTO>> getUserReservations(@PathVariable UUID id, Pageable pageable) {
        userService.findById(id);
        Page<Reservation> reservationPage = reservationService.findByUser(id, pageable);
        Page<ReservationDTO> reservationDTOPage = reservationPage.map(ReservationDTO::fromEntity);
        return ResponseEntity.ok(PagedResponse.of(reservationDTOPage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/statut")
    @Audited(action = "VALIDATION_COMPTE")
    @Operation(summary = "Valide le statut d'un utilisateur", description = "Change le statut d'un utilisateur (PENDING -> ACTIVE/REJECTED) - SECRETARY/ADMIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<UserDTO> updateUserStatut(
            @PathVariable UUID id,
            @RequestBody User.Statut newStatut,
            Authentication authentication) {
        
        delegationPermissionService.checkPermissionOrDelegatedPermission(
            authentication, 
            Delegation.Permission.UPDATE_USER_STATUT,
            "Vous n'avez pas la permission de modifier le statut d'un utilisateur"
        );
        User updatedUser = userService.updateStatut(id, newStatut);
        return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
    }
}
