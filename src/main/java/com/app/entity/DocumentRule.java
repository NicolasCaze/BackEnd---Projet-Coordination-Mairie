package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Groupe.TypeGroupe typeGroupe;

    @Enumerated(EnumType.STRING)
    private Groupe.TypeExoneration typeExoneration;

    @Column(nullable = false)
    private String document;

    private String description;

    @Column(name = "creer_le", updatable = false)
    private LocalDateTime creer_le;

    @PrePersist
    public void prePersist() {
        this.creer_le = LocalDateTime.now();
    }
}
