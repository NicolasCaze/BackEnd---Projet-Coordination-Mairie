package com.app.dto;

import com.app.entity.Bien;
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
public class BienDTO {
    
    private UUID id_bien;
    private String nom;
    private String description;
    private Boolean estVisible;
    private LocalDateTime creerLe;
    private UUID id_cat_bien;
    private String nom_cat_bien;
    
    public static BienDTO fromEntity(Bien bien) {
        return BienDTO.builder()
                .id_bien(bien.getId_bien())
                .nom(bien.getNom())
                .description(bien.getDescription())
                .estVisible(bien.getEstVisible())
                .creerLe(bien.getCreerLe())
                .id_cat_bien(bien.getCatBien() != null ? bien.getCatBien().getId_cat_bien() : null)
                .nom_cat_bien(bien.getCatBien() != null ? bien.getCatBien().getNom() : null)
                .build();
    }
}
