package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pieces_justificatives")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJustificative {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_piece;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filepath;

    private LocalDateTime uploaded_at;

    private LocalDateTime sent_at;

    private LocalDateTime deleted_at;

    @PrePersist
    public void prePersist() {
        this.uploaded_at = LocalDateTime.now();
    }
}
