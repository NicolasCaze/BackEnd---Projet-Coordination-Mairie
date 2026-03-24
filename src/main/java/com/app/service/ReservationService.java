package com.app.service;

import com.app.entity.Reservation;
import com.app.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final GroupeService groupeService;
    private final BienService bienService;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation findById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée : " + id));
    }

    public List<Reservation> findByUser(UUID id_user) {
        userService.findById(id_user);
        return reservationRepository.findByUserIdUser(id_user);
    }

    public List<Reservation> findByGroupe(UUID id_groupe) {
        groupeService.findById(id_groupe);
        return reservationRepository.findByGroupeIdGroupe(id_groupe);
    }

    public List<Reservation> findByBien(UUID id_bien) {
        bienService.findById(id_bien);
        return reservationRepository.findByBienIdBien(id_bien);
    }

    public Reservation create(Reservation reservation) {
        // Vérifications de base
        userService.findById(reservation.getUser().getId_user());
        bienService.findById(reservation.getBien().getId_bien());

        if (reservation.getDate_debut().isAfter(reservation.getDate_fin())) {
            throw new RuntimeException("La date de début doit être avant la date de fin");
        }

        reservation.setStatut(Reservation.Statut.EN_ATTENTE);
        reservation.setStatut_caution(Reservation.StatutCaution.NON_REQUISE);
        return reservationRepository.save(reservation);
    }

    public Reservation update(UUID id, Reservation updated) {
        Reservation reservation = findById(id);
        reservation.setDate_debut(updated.getDate_debut());
        reservation.setDate_fin(updated.getDate_fin());
        reservation.setGroupe(updated.getGroupe());
        return reservationRepository.save(reservation);
    }

    public Reservation updateStatut(UUID id, Reservation.Statut statut) {
        Reservation reservation = findById(id);
        reservation.setStatut(statut);
        return reservationRepository.save(reservation);
    }

    public Reservation updateStatutCaution(UUID id, Reservation.StatutCaution statutCaution) {
        Reservation reservation = findById(id);
        reservation.setStatut_caution(statutCaution);
        return reservationRepository.save(reservation);
    }

    public void delete(UUID id) {
        findById(id);
        reservationRepository.deleteById(id);
    }
}
