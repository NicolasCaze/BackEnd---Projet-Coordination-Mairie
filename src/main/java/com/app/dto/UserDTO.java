package com.app.dto;

import com.app.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    private UUID id_user;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Boolean is_resident;
    private User.Role role;
    private User.Statut statut;
    
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id_user(user.getId_user())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .is_resident(user.getIs_resident())
                .role(user.getRole())
                .statut(user.getStatut())
                .build();
    }
}
