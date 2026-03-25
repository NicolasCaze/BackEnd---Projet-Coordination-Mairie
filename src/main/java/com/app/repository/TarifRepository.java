package com.app.repository;

import com.app.entity.Tarif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface TarifRepository extends JpaRepository<Tarif, UUID> {
    Optional<Tarif> findByBienIdBien(UUID id_bien);
}
