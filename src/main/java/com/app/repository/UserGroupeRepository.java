package com.app.repository;

import com.app.entity.UserGroupe;
import com.app.entity.UserGroupeId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserGroupeRepository extends JpaRepository<UserGroupe, UserGroupeId> {
    
    Page<UserGroupe> findByGroupeIdGroupe(UUID id_groupe, Pageable pageable);
    
    List<UserGroupe> findByGroupeIdGroupe(UUID id_groupe);
    
    List<UserGroupe> findByUserIdUser(UUID id_user);
}
