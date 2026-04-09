package com.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "type_groupe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeGroupe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id_type_groupe;
    
    @Column(nullable = false, unique = true)
    private String nom;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
