package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "groupes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_groupe;

    @Column(nullable = false)
    private String nom;

    private String description;

    @Enumerated(EnumType.STRING)
    private TypeGroupe type_groupe;

    @Enumerated(EnumType.STRING)
    private TypeExoneration type_exoneration;

    private Boolean est_caution;
    private Boolean est_piece_justif;
    private Boolean est_paye;
    private Integer niveau_tarif;

    @Column(name = "creer_le", updatable = false)
    private LocalDateTime creer_le;

    @PrePersist
    public void prePersist() {
        this.creer_le = LocalDateTime.now();
    }

    public enum TypeGroupe {
        ASSOCIATION, CONSEIL_MUNICIPAL, ENTREPRISE, PARTICULIER
    }

    public enum TypeExoneration {
        ASSO, SOCIAL, MANDAT_ELECTIF, EXONERE_CAUTION
    }
}
