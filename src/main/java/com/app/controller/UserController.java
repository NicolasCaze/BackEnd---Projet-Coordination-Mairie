package com.app.controller;

import com.app.dto.UserDTO;
import com.app.entity.User;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
                .role(userDTO.getRole())
                .statut(userDTO.getStatut())
                .build();
        
        User createdUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserDTO.fromEntity(createdUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDTO userDTO) {
        User user = User.builder()
                .nom(userDTO.getNom())
                .prenom(userDTO.getPrenom())
                .email(userDTO.getEmail())
                .mot_de_passe(userDTO.getMot_de_passe())
                .telephone(userDTO.getTelephone())
                .is_resident(userDTO.getIs_resident())
                .is_tutored(userDTO.getIs_tutored())
                .role(userDTO.getRole())
                .statut(userDTO.getStatut())
                .build();
        
        User updatedUser = userService.update(id, user);
        return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
