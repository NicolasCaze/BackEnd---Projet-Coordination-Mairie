package com.app.repository;

import com.app.entity.TypeGroupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TypeGroupeRepository extends JpaRepository<TypeGroupe, UUID> {
    Optional<TypeGroupe> findByNom(String nom);
    boolean existsByNom(String nom);
}
