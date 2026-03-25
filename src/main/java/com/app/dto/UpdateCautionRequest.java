package com.app.dto;

import com.app.entity.Reservation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCautionRequest {
    
    @NotNull(message = "Le statut de caution est obligatoire")
    private Reservation.StatutCaution statut_caution;
}
