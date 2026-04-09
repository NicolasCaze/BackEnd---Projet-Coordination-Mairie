package com.app.service;

import com.app.entity.Groupe;
import com.app.entity.Reservation;
import com.app.exception.ReservationConflictException;
import com.app.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final GroupeService groupeService;
    private final BienService bienService;
    
    public ReservationService(ReservationRepository reservationRepository, UserService userService, 
                           GroupeService groupeService, BienService bienService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.groupeService = groupeService;
        this.bienService = bienService;
    }

    public Page<Reservation> findAll(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }

    public Page<Reservation> findByStatut(Reservation.Statut statut, Pageable pageable) {
        return reservationRepository.findByStatut(statut, pageable);
    }
    
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Page<Reservation> findByUser(UUID id_user, Pageable pageable) {
        userService.findById(id_user);
        return reservationRepository.findByUserIdUser(id_user, pageable);
    }
    
    public List<Reservation> findByUser(UUID id_user) {
        userService.findById(id_user);
        return reservationRepository.findByUserIdUser(id_user);
    }

    public Page<Reservation> findByGroupe(UUID id_groupe, Pageable pageable) {
        groupeService.findById(id_groupe);
        return reservationRepository.findByGroupeIdGroupe(id_groupe, pageable);
    }
    
    public List<Reservation> findByGroupe(UUID id_groupe) {
        groupeService.findById(id_groupe);
        return reservationRepository.findByGroupeIdGroupe(id_groupe);
    }

    public Page<Reservation> findByBien(UUID id_bien, Pageable pageable) {
        bienService.findById(id_bien);
        return reservationRepository.findByBienIdBien(id_bien, pageable);
    }
    
    public List<Reservation> findByBien(UUID id_bien) {
        bienService.findById(id_bien);
        return reservationRepository.findByBienIdBien(id_bien);
    }

    public Reservation findById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée : " + id));
    }

    public Reservation create(Reservation reservation) {
        // Vérifications de base
        userService.findById(reservation.getUser().getId_user());
        var bien = bienService.findById(reservation.getBien().getId_bien());

        if (reservation.getDate_debut().isAfter(reservation.getDate_fin())) {
            throw new RuntimeException("La date de début doit être avant la date de fin");
        }

        // Vérification des conflits de créneau
        List<Reservation> conflits = reservationRepository.findConflictingReservations(
                reservation.getBien().getId_bien(),
                reservation.getDate_debut(),
                reservation.getDate_fin()
        );
        
        if (!conflits.isEmpty()) {
            throw new ReservationConflictException("Conflit de créneau : ce bien est déjà réservé pour cette période");
        }

        // Calcul automatique du prix en fonction du niveau tarifaire
        Double prix = calculerPrix(reservation, bien);
        reservation.setPrix(prix);

        reservation.setStatut(Reservation.Statut.EN_ATTENTE);
        reservation.setStatut_caution(Reservation.StatutCaution.NON_REQUISE);
        
        // Logique d'exonération automatique de la caution
        boolean estExonereCaution = false;
        if (reservation.getGroupe() != null && reservation.getGroupe().getType_exoneration() != null) {
            estExonereCaution = reservation.getGroupe().getType_exoneration() == Groupe.TypeExoneration.EXONERE_CAUTION;
        }
        
        reservation.setEst_caution(estExonereCaution);
        reservation.setEst_valide(false);
        
        return reservationRepository.save(reservation);
    }
    
    /**
     * Calcule le prix de la réservation en fonction du niveau tarifaire
     * @param reservation La réservation
     * @param bien Le bien réservé
     * @return Le prix calculé
     */
    private Double calculerPrix(Reservation reservation, com.app.entity.Bien bien) {
        if (bien.getTarif() == null) {
            return 0.0; // Pas de tarif défini = gratuit
        }
        
        Integer niveauTarif;
        
        // Déterminer le niveau tarifaire applicable
        if (reservation.getGroupe() != null) {
            // Réservation pour un groupe : utiliser le niveau du groupe
            niveauTarif = reservation.getGroupe().getNiveau_tarif();
        } else {
            // Réservation individuelle : utiliser le niveau de l'utilisateur
            niveauTarif = reservation.getUser().getNiveau_tarif();
        }
        
        // Si aucun niveau n'est défini, utiliser le niveau 1 par défaut
        if (niveauTarif == null) {
            niveauTarif = 1;
        }
        
        // Récupérer le prix correspondant au niveau
        var tarif = bien.getTarif();
        return switch (niveauTarif) {
            case 1 -> tarif.getNiveau_1();
            case 2 -> tarif.getNiveau_2();
            case 3 -> tarif.getNiveau_3();
            case 4 -> tarif.getNiveau_4();
            case 5 -> tarif.getNiveau_5();
            default -> tarif.getNiveau_1(); // Par défaut, niveau 1
        };
    }

    public Reservation update(UUID id, Reservation updated) {
        Reservation reservation = findById(id);
        
        // Vérification des conflits de créneau (en excluant la réservation actuelle)
        List<Reservation> conflits = reservationRepository.findConflictingReservationsExcludingCurrent(
                reservation.getBien().getId_bien(),
                id,
                updated.getDate_debut(),
                updated.getDate_fin()
        );
        
        if (!conflits.isEmpty()) {
            throw new ReservationConflictException("Conflit de créneau : ce bien est déjà réservé pour cette période");
        }
        
        reservation.setDate_debut(updated.getDate_debut());
        reservation.setDate_fin(updated.getDate_fin());
        reservation.setGroupe(updated.getGroupe());
        return reservationRepository.save(reservation);
    }

    public Reservation updateStatut(UUID id, Reservation.Statut statut) {
        Reservation reservation = findById(id);
        
        // Si on confirme une réservation, vérifier les conflits
        if (statut == Reservation.Statut.CONFIRMEE && reservation.getStatut() != Reservation.Statut.CONFIRMEE) {
            List<Reservation> conflits = reservationRepository.findConflictingReservationsExcludingCurrent(
                    reservation.getBien().getId_bien(),
                    id,
                    reservation.getDate_debut(),
                    reservation.getDate_fin()
            );
            
            if (!conflits.isEmpty()) {
                throw new ReservationConflictException("Conflit de créneau : ce bien est déjà réservé pour cette période");
            }
        }
        
        reservation.setStatut(statut);
        return reservationRepository.save(reservation);
    }

    public Reservation updateEstCaution(UUID id, Boolean estCaution) {
        Reservation reservation = findById(id);
        reservation.setEst_caution(estCaution);
        return reservationRepository.save(reservation);
    }

    public Reservation updateEstValide(UUID id, Boolean estValide) {
        Reservation reservation = findById(id);
        
        if (estValide) {
            // Si on valide la réservation, vérifier les conflits
            List<Reservation> conflits = reservationRepository.findConflictingReservationsExcludingCurrent(
                    reservation.getBien().getId_bien(),
                    id,
                    reservation.getDate_debut(),
                    reservation.getDate_fin()
            );
            
            if (!conflits.isEmpty()) {
                throw new ReservationConflictException("Conflit de créneau : ce bien est déjà réservé pour cette période");
            }
            reservation.setStatut(Reservation.Statut.CONFIRMEE);
        } else {
            reservation.setStatut(Reservation.Statut.ANNULEE);
        }
        
        reservation.setEst_valide(estValide);
        return reservationRepository.save(reservation);
    }

    public void delete(UUID id) {
        findById(id);
        reservationRepository.deleteById(id);
    }
}
