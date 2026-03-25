package com.app.dto;

import com.app.entity.Groupe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRuleRequest {
    private Groupe.TypeGroupe typeGroupe;
    private Groupe.TypeExoneration typeExoneration;
    private String document;
    private String description;
}
