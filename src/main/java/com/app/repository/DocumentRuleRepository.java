package com.app.repository;

import com.app.entity.DocumentRule;
import com.app.entity.Groupe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRuleRepository extends JpaRepository<DocumentRule, Long> {
    
    Page<DocumentRule> findAll(Pageable pageable);

    @Query("SELECT dr FROM DocumentRule dr WHERE " +
           "(dr.typeGroupe = :typeGroupe OR dr.typeGroupe IS NULL) AND " +
           "(dr.typeExoneration = :typeExoneration OR dr.typeExoneration IS NULL)")
    List<DocumentRule> findByTypeGroupeAndTypeExoneration(
            @Param("typeGroupe") Groupe.TypeGroupe typeGroupe,
            @Param("typeExoneration") Groupe.TypeExoneration typeExoneration
    );

    List<DocumentRule> findByTypeGroupe(Groupe.TypeGroupe typeGroupe);

    List<DocumentRule> findByTypeExoneration(Groupe.TypeExoneration typeExoneration);
}
