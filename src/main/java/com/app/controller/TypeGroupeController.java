package com.app.controller;

import com.app.entity.TypeGroupe;
import com.app.service.TypeGroupeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/type-groupes")
@RequiredArgsConstructor
@Tag(name = "Types de Groupes", description = "Gestion des types de groupes")
public class TypeGroupeController {
    
    private final TypeGroupeService typeGroupeService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Récupérer tous les types de groupes")
    public ResponseEntity<List<TypeGroupe>> getAllTypeGroupes() {
        return ResponseEntity.ok(typeGroupeService.findAll());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Récupérer un type de groupe par ID")
    public ResponseEntity<TypeGroupe> getTypeGroupeById(@PathVariable UUID id) {
        return ResponseEntity.ok(typeGroupeService.findById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Créer un nouveau type de groupe")
    public ResponseEntity<TypeGroupe> createTypeGroupe(@Valid @RequestBody TypeGroupe typeGroupe) {
        TypeGroupe created = typeGroupeService.create(typeGroupe);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Modifier un type de groupe")
    public ResponseEntity<TypeGroupe> updateTypeGroupe(
            @PathVariable UUID id,
            @Valid @RequestBody TypeGroupe typeGroupe) {
        TypeGroupe updated = typeGroupeService.update(id, typeGroupe);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Supprimer un type de groupe")
    public ResponseEntity<Void> deleteTypeGroupe(@PathVariable UUID id) {
        typeGroupeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
