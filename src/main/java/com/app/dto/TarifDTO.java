package com.app.dto;

import com.app.entity.Tarif;
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
public class TarifDTO {
    private UUID id_tarif;
    private UUID id_bien;
    private Double niveau_1;
    private Double niveau_2;
    private Double niveau_3;
    private Double niveau_4;
    private Double niveau_5;
    private LocalDateTime creerLe;

    public static TarifDTO fromEntity(Tarif tarif) {
        if (tarif == null) {
            return null;
        }

        return TarifDTO.builder()
                .id_tarif(tarif.getId_tarif())
                .id_bien(tarif.getBien() != null ? tarif.getBien().getId_bien() : null)
                .niveau_1(tarif.getNiveau_1())
                .niveau_2(tarif.getNiveau_2())
                .niveau_3(tarif.getNiveau_3())
                .niveau_4(tarif.getNiveau_4())
                .niveau_5(tarif.getNiveau_5())
                .creerLe(tarif.getCreerLe())
                .build();
    }

    public static Tarif toEntity(TarifDTO dto) {
        if (dto == null) {
            return null;
        }

        Tarif tarif = new Tarif();
        tarif.setId_tarif(dto.getId_tarif());
        tarif.setNiveau_1(dto.getNiveau_1());
        tarif.setNiveau_2(dto.getNiveau_2());
        tarif.setNiveau_3(dto.getNiveau_3());
        tarif.setNiveau_4(dto.getNiveau_4());
        tarif.setNiveau_5(dto.getNiveau_5());
        tarif.setCreerLe(dto.getCreerLe());
        
        return tarif;
    }
}
