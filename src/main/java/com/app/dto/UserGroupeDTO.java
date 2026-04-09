package com.app.dto;

import com.app.entity.UserGroupe;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupeDTO {
    
    private UUID id_user;
    private UUID id_groupe;
    private UserGroupe.Status status;
    private LocalDateTime rejoint_le;
    
    // Informations du membre pour l'affichage
    private String nom;
    private String prenom;
    private String email;
    private UserGroupe.RoleGroupe roleGroupe;
    
    public static UserGroupeDTO fromEntity(UserGroupe userGroupe) {
        return UserGroupeDTO.builder()
                .id_user(userGroupe.getUser() != null ? userGroupe.getUser().getId_user() : null)
                .id_groupe(userGroupe.getGroupe() != null ? userGroupe.getGroupe().getId_groupe() : null)
                .status(userGroupe.getStatus())
                .rejoint_le(userGroupe.getRejoint_le())
                .nom(userGroupe.getUser() != null ? userGroupe.getUser().getNom() : null)
                .prenom(userGroupe.getUser() != null ? userGroupe.getUser().getPrenom() : null)
                .email(userGroupe.getUser() != null ? userGroupe.getUser().getEmail() : null)
                .roleGroupe(userGroupe.getRoleGroupe())
                .build();
    }
}
