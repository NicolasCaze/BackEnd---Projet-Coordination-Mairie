package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_groupe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupe {

    @EmbeddedId
    private UserGroupeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id_user")
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id_groupe")
    @JoinColumn(name = "id_groupe")
    private Groupe groupe;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_groupe")
    private RoleGroupe roleGroupe;

    @Column(name = "rejoint_le", updatable = false)
    private LocalDateTime rejoint_le;

    @PrePersist
    public void prePersist() {
        this.rejoint_le = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.ACTIF;
        }
        if (this.roleGroupe == null) {
            this.roleGroupe = RoleGroupe.MEMBRE;
        }
    }

    public enum Status {
        ACTIF, INACTIF, SUSPENDU
    }

    public enum RoleGroupe {
        ADMIN, MEMBRE
    }
}
