package com.app.dto;

import com.app.entity.Groupe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRuleResponse {
    private Long id;
    private Groupe.TypeGroupe typeGroupe;
    private Groupe.TypeExoneration typeExoneration;
    private String document;
    private String description;
    private LocalDateTime creer_le;
}
