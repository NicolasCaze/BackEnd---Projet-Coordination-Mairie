package com.app.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserGroupeId implements Serializable {
    
    private UUID id_user;
    private UUID id_groupe;
}
