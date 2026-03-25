package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "delegations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delegation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "from_user_id", nullable = false)
    private UUID fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private UUID toUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Permission permission;

    @Column(name = "delegated_at", updatable = false)
    private LocalDateTime delegatedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Builder.Default
    private Boolean active = true;

    @PrePersist
    public void prePersist() {
        this.delegatedAt = LocalDateTime.now();
    }

    public enum Permission {
        DELETE_RESERVATION,
        DELETE_USER,
        DELETE_GROUPE,
        CREATE_GROUPE,
        UPDATE_USER_STATUT,
        UPDATE_RESERVATION_VALIDATION,
        UPDATE_RESERVATION_CAUTION
    }
}
