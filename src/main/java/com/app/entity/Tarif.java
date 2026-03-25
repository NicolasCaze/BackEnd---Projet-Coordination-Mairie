package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tarifs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarif {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_tarif;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bien", nullable = false)
    private Bien bien;

    @Column(name = "niveau_1")
    private Double niveau_1;

    @Column(name = "niveau_2")
    private Double niveau_2;

    @Column(name = "niveau_3")
    private Double niveau_3;

    @Column(name = "niveau_4")
    private Double niveau_4;

    @Column(name = "niveau_5")
    private Double niveau_5;

    @Column(name = "creer_le", updatable = false)
    private LocalDateTime creerLe;

    @PrePersist
    public void prePersist() {
        this.creerLe = LocalDateTime.now();
    }
}
