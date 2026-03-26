package com.app.controller;

import com.app.annotation.Audited;
import com.app.dto.UserDTO;
import com.app.dto.ReservationDTO;
import com.app.entity.Reservation;
import com.app.entity.User;
import com.app.entity.Delegation;
import com.app.service.DelegationPermissionService;
import com.app.service.ReservationService;
import com.app.service.UserService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ReservationService reservationService;
    private final DelegationPermissionService delegationPermissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
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
    public ResponseEntity<List<ReservationDTO>> getUserReservations(@PathVariable UUID id) {
        userService.findById(id);
        List<Reservation> reservations = reservationService.findByUser(id);
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservationDTOs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id, Authentication authentication) {
        delegationPermissionService.checkPermissionOrDelegatedPermission(
            authentication, 
            Delegation.Permission.DELETE_USER,
            "Vous n'avez pas la permission de supprimer un utilisateur"
        );
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/statut")
    @Audited(action = "VALIDATION_COMPTE")
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
