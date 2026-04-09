package com.app.repository;

import com.app.entity.Bien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BienRepository extends JpaRepository<Bien, UUID> {

    @Query("SELECT b FROM Bien b WHERE b.estVisible = true")
    Page<Bien> findVisibleBiens(Pageable pageable);

    @Query("SELECT b FROM Bien b WHERE b.estVisible = true AND " +
           "(LOWER(b.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Bien> findVisibleBiensWithFilters(@Param("search") String search, Pageable pageable);

    @Query("SELECT b FROM Bien b WHERE b.estVisible = true AND b.catBien.id_cat_bien = :categoryId")
    Page<Bien> findVisibleBiensByCategory(@Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT b FROM Bien b WHERE b.estVisible = true AND " +
           "b.catBien.id_cat_bien = :categoryId AND " +
           "(LOWER(b.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Bien> findVisibleBiensByCategoryAndSearch(@Param("categoryId") UUID categoryId, 
                                                  @Param("search") String search, 
                                                  Pageable pageable);

    List<Bien> findByEstVisibleTrue();
    
    @Query("SELECT b FROM Bien b WHERE b.catBien.id_cat_bien = :categoryId")
    List<Bien> findByCatBienIdCatBien(@Param("categoryId") UUID categoryId);
}
