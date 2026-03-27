package com.app.dto;

import com.app.entity.CatBien;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatBienDTO {

    private UUID id_cat_bien;
    private String nom;
    private String description;
    private LocalDateTime creerLe;

    public static CatBienDTO fromEntity(CatBien catBien) {
        return CatBienDTO.builder()
                .id_cat_bien(catBien.getId_cat_bien())
                .nom(catBien.getNom())
                .description(catBien.getDescription())
                .creerLe(catBien.getCreerLe())
                .build();
    }

    public static CatBien toEntity(CatBienDTO dto) {
        return CatBien.builder()
                .id_cat_bien(dto.getId_cat_bien())
                .nom(dto.getNom())
                .description(dto.getDescription())
                .build();
    }
}
