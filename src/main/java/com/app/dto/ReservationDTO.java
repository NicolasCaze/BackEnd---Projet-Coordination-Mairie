package com.app.dto;

import com.app.entity.Reservation;
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
public class ReservationDTO {
    
    private UUID id_reservation;
    private UUID id_user;
    private UUID id_bien;
    private UUID id_groupe;
    private LocalDateTime date_debut;
    private LocalDateTime date_fin;
    private Reservation.Statut statut;
    private Reservation.StatutCaution statut_caution;
    private LocalDateTime created_at;
    
    public static ReservationDTO fromEntity(Reservation reservation) {
        return ReservationDTO.builder()
                .id_reservation(reservation.getId_reservation())
                .id_user(reservation.getUser() != null ? reservation.getUser().getId_user() : null)
                .id_bien(reservation.getBien() != null ? reservation.getBien().getId_bien() : null)
                .id_groupe(reservation.getGroupe() != null ? reservation.getGroupe().getId_groupe() : null)
                .date_debut(reservation.getDate_debut())
                .date_fin(reservation.getDate_fin())
                .statut(reservation.getStatut())
                .statut_caution(reservation.getStatut_caution())
                .created_at(reservation.getCreated_at())
                .build();
    }
}
