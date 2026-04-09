package com.app.repository;

import com.app.entity.UserGroupe;
import com.app.entity.UserGroupeId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserGroupeRepository extends JpaRepository<UserGroupe, UserGroupeId> {
    
    @Query("SELECT ug FROM UserGroupe ug WHERE ug.groupe.id_groupe = :idGroupe")
    Page<UserGroupe> findByGroupeIdGroupe(@Param("idGroupe") UUID id_groupe, Pageable pageable);
    
    @Query("SELECT ug FROM UserGroupe ug WHERE ug.groupe.id_groupe = :idGroupe")
    List<UserGroupe> findByGroupeIdGroupe(@Param("idGroupe") UUID id_groupe);
    
    @Query("SELECT ug FROM UserGroupe ug WHERE ug.user.id_user = :idUser")
    List<UserGroupe> findByUserIdUser(@Param("idUser") UUID id_user);
}
