package com.app.repository;

import com.app.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Page<Reservation> findAll(Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.user.id_user = :idUser")
    Page<Reservation> findByUserIdUser(@Param("idUser") UUID id_user, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.groupe.id_groupe = :idGroupe")
    Page<Reservation> findByGroupeIdGroupe(@Param("idGroupe") UUID id_groupe, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.bien.id_bien = :idBien")
    Page<Reservation> findByBienIdBien(@Param("idBien") UUID id_bien, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.user.id_user = :idUser")
    List<Reservation> findByUserIdUser(@Param("idUser") UUID id_user);
    
    @Query("SELECT r FROM Reservation r WHERE r.groupe.id_groupe = :idGroupe")
    List<Reservation> findByGroupeIdGroupe(@Param("idGroupe") UUID id_groupe);
    
    @Query("SELECT r FROM Reservation r WHERE r.bien.id_bien = :idBien")
    List<Reservation> findByBienIdBien(@Param("idBien") UUID id_bien);

    @Query("SELECT r FROM Reservation r WHERE r.bien.id_bien = :bienId " +
           "AND r.statut IN ('CONFIRMEE', 'EN_ATTENTE') " +
           "AND ((r.date_debut < :dateFin AND r.date_fin > :dateDebut))")
    List<Reservation> findConflictingReservations(
            @Param("bienId") UUID bienId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );

    @Query("SELECT r FROM Reservation r WHERE r.bien.id_bien = :bienId " +
           "AND r.id_reservation != :reservationId " +
           "AND r.statut IN ('CONFIRMEE', 'EN_ATTENTE') " +
           "AND ((r.date_debut < :dateFin AND r.date_fin > :dateDebut))")
    List<Reservation> findConflictingReservationsExcludingCurrent(
            @Param("bienId") UUID bienId,
            @Param("reservationId") UUID reservationId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );

    @Query("SELECT r FROM Reservation r WHERE r.bien.id_bien = :bienId " +
           "AND r.statut IN ('CONFIRMEE', 'EN_ATTENTE') " +
           "ORDER BY r.date_debut ASC")
    List<Reservation> findActiveReservationsByBien(@Param("bienId") UUID bienId);

    Page<Reservation> findByStatut(Reservation.Statut statut, Pageable pageable);
}
