package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "biens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bien {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_bien;

    @Column(nullable = false)
    private String nom;

    private String description;

    @Column(nullable = false)
    private Boolean estVisible;

    @Column(name = "creer_le", nullable = false, updatable = false)
    private LocalDateTime creerLe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cat_bien")
    private CatBien catBien;

    @PrePersist
    public void prePersist() {
        this.creerLe = LocalDateTime.now();
        if (this.estVisible == null) {
            this.estVisible = true;
        }
    }
}
