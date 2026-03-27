package com.app.controller;

import com.app.dto.CatBienDTO;
import com.app.entity.CatBien;
import com.app.service.CatBienService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cat-biens")
@RequiredArgsConstructor
@Tag(name = "Catégories de biens", description = "Gestion des catégories de biens municipaux")
public class CatBienController {

    private final CatBienService catBienService;

    @GetMapping
    @Operation(summary = "Liste toutes les catégories", description = "Récupère la liste complète des catégories de biens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée avec succès")
    })
    public ResponseEntity<List<CatBienDTO>> getAllCategories() {
        List<CatBien> categories = catBienService.findAll();
        List<CatBienDTO> categoryDTOs = categories.stream()
                .map(CatBienDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupère une catégorie par ID", description = "Retourne les détails d'une catégorie spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie trouvée"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<CatBienDTO> getCategoryById(@PathVariable @Parameter(description = "ID de la catégorie") UUID id) {
        CatBien category = catBienService.findById(id);
        return ResponseEntity.ok(CatBienDTO.fromEntity(category));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crée une nouvelle catégorie", description = "Crée une catégorie de bien (ADMIN uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Catégorie créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<CatBienDTO> createCategory(@Valid @RequestBody CatBienDTO catBienDTO) {
        CatBien catBien = CatBienDTO.toEntity(catBienDTO);
        CatBien created = catBienService.create(catBien);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CatBienDTO.fromEntity(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Met à jour une catégorie", description = "Modifie les informations d'une catégorie existante (ADMIN uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie mise à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<CatBienDTO> updateCategory(@PathVariable @Parameter(description = "ID de la catégorie") UUID id,
                                                      @Valid @RequestBody CatBienDTO catBienDTO) {
        CatBien catBien = CatBienDTO.toEntity(catBienDTO);
        CatBien updated = catBienService.update(id, catBien);
        return ResponseEntity.ok(CatBienDTO.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprime une catégorie", description = "Supprime définitivement une catégorie (ADMIN uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Catégorie supprimée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable @Parameter(description = "ID de la catégorie") UUID id) {
        catBienService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
