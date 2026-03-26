package com.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "impersonation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpersonationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @Column(name = "impersonated_user_id", nullable = false)
    private UUID impersonatedUserId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
