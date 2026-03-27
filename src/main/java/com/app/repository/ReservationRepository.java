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
    
    Page<Reservation> findByUserIdUser(UUID id_user, Pageable pageable);
    
    Page<Reservation> findByGroupeIdGroupe(UUID id_groupe, Pageable pageable);
    
    Page<Reservation> findByBienIdBien(UUID id_bien, Pageable pageable);

    List<Reservation> findByUserIdUser(UUID id_user);
    
    List<Reservation> findByGroupeIdGroupe(UUID id_groupe);
    
    List<Reservation> findByBienIdBien(UUID id_bien);

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
}
