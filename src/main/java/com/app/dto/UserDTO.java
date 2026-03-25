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
    private String mot_de_passe;
    private String telephone;
    private Boolean is_resident;
    private Boolean is_tutored;
    private Integer niveau_tarif;
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
                .is_tutored(user.getIs_tutored())
                .niveau_tarif(user.getNiveau_tarif())
                .role(user.getRole())
                .statut(user.getStatut())
                .build();
    }
}
