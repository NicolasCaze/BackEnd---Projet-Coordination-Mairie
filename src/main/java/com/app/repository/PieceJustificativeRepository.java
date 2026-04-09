package com.app.repository;

import com.app.entity.PieceJustificative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, UUID> {
    @Query("SELECT pj FROM PieceJustificative pj WHERE pj.reservation.id_reservation = :idReservation")
    List<PieceJustificative> findByReservationIdReservation(@Param("idReservation") UUID id_reservation);
}
