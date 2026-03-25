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
    private Boolean is_resident;
    private Boolean is_tutored;

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
        ADMIN, USER, MODERATEUR, TUTORED
    }

    public enum Statut {
        ACTIF, INACTIF, SUSPENDU
    }
}
