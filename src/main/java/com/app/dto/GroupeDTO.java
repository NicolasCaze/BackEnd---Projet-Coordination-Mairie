package com.app.dto;

import com.app.entity.Groupe;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupeDTO {
    
    private UUID id_groupe;
    private String nom;
    private String description;
    private Groupe.TypeGroupe type_groupe;
    private Groupe.TypeExoneration type_exoneration;
    private Integer niveau_tarif;
    private LocalDateTime creer_le;
    private List<MembreInfo> membres;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MembreInfo {
        private UUID id_user;
        private String nom;
        private String prenom;
        private String email;
        private String roleGroupe;
    }
    
    public static GroupeDTO fromEntity(Groupe groupe) {
        return GroupeDTO.builder()
                .id_groupe(groupe.getId_groupe())
                .nom(groupe.getNom())
                .description(groupe.getDescription())
                .type_groupe(groupe.getType_groupe())
                .type_exoneration(groupe.getType_exoneration())
                .niveau_tarif(groupe.getNiveau_tarif())
                .creer_le(groupe.getCreer_le())
                .build();
    }
}
