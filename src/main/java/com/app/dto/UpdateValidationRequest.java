package com.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateValidationRequest {
    
    @NotNull(message = "Le statut de validation est obligatoire")
    private Boolean est_valide;
}
