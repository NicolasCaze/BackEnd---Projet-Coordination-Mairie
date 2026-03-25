package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cat_biens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatBien {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_cat_bien;

    @Column(nullable = false, unique = true)
    private String nom;

    private String description;

    @Column(name = "creer_le", nullable = false, updatable = false)
    private LocalDateTime creerLe;

    @OneToMany(mappedBy = "catBien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bien> biens;

    @PrePersist
    public void prePersist() {
        this.creerLe = LocalDateTime.now();
    }
}
