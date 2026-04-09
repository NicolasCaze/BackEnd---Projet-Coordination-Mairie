package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_user;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    private String mot_de_passe;
    private String telephone;
    private String adresse;
    private Boolean is_resident;
    private Boolean is_tutored;
    private Integer niveau_tarif;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    @Column(updatable = false)
    private LocalDateTime created_at;

    @PrePersist
    public void prePersist() {
        this.created_at = LocalDateTime.now();
    }

    public enum Role {
        SUPER_ADMIN, ADMIN, USER, MODERATEUR, TUTORED, SECRETARY
    }

    public enum Statut {
        PENDING, ACTIVE, REJECTED, ACTIF, INACTIF, SUSPENDU
    }
}
