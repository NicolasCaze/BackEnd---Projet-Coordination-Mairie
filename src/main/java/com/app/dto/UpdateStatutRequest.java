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
public class UpdateStatutRequest {
    
    @NotNull(message = "Le statut est obligatoire")
    private Reservation.Statut statut;
}
