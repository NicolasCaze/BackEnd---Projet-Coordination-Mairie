package com.app.repository;

import com.app.entity.Tarif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface TarifRepository extends JpaRepository<Tarif, UUID> {
    @Query("SELECT t FROM Tarif t WHERE t.bien.id_bien = :idBien")
    Optional<Tarif> findByBienIdBien(@Param("idBien") UUID id_bien);
}
