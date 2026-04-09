package com.app.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
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
public class CreateReservationRequest {
    
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private UUID id_user;
    
    private UUID id_groupe;
    
    @NotNull(message = "L'ID du bien est obligatoire")
    private UUID id_bien;
    
    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    private LocalDateTime dateFin;
    
    private String motif;
    
    private Integer nombrePersonnes;
}
