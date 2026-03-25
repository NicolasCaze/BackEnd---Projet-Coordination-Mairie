package com.app.repository;

import com.app.entity.CatBien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CatBienRepository extends JpaRepository<CatBien, UUID> {
}
