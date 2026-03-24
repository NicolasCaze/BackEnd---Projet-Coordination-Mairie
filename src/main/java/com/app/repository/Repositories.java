// UserRepository.java
package com.app.repository;

import com.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}


// GroupeRepository.java
package com.app.repository;

import com.app.entity.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GroupeRepository extends JpaRepository<Groupe, UUID> {
}


// UserGroupeRepository.java
package com.app.repository;

import com.app.entity.UserGroupe;
import com.app.entity.UserGroupeId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserGroupeRepository extends JpaRepository<UserGroupe, UserGroupeId> {
    List<UserGroupe> findByIdGroupe(UUID id_groupe);
    List<UserGroupe> findByIdUser(UUID id_user);
}


// BienRepository.java
package com.app.repository;

import com.app.entity.Bien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BienRepository extends JpaRepository<Bien, UUID> {
    List<Bien> findByEstVisible(Boolean estVisible);
}


// CatBienRepository.java
package com.app.repository;

import com.app.entity.CatBien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CatBienRepository extends JpaRepository<CatBien, UUID> {
}


// TarifRepository.java
package com.app.repository;

import com.app.entity.Tarif;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TarifRepository extends JpaRepository<Tarif, UUID> {
    Optional<Tarif> findByBienIdBien(UUID id_bien);
}


// ReservationRepository.java
package com.app.repository;

import com.app.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByUserIdUser(UUID id_user);
    List<Reservation> findByGroupeIdGroupe(UUID id_groupe);
    List<Reservation> findByBienIdBien(UUID id_bien);
}
