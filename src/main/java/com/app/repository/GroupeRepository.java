package com.app.repository;

import com.app.entity.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupeRepository extends JpaRepository<Groupe, UUID> {
}
