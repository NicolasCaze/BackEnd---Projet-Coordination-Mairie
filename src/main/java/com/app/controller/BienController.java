package com.app.controller;

import com.app.dto.BienDTO;
import com.app.dto.ReservationDTO;
import com.app.entity.Bien;
import com.app.entity.CatBien;
import com.app.entity.Reservation;
import com.app.service.BienService;
import com.app.service.CatBienService;
import com.app.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/biens")
@RequiredArgsConstructor
@Tag(name = "Biens", description = "Gestion des biens municipaux réservables")
public class BienController {

    private final BienService bienService;
    private final CatBienService catBienService;
    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "Liste tous les biens", description = "Récupère la liste paginée des biens visibles avec filtres optionnels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des biens récupérée avec succès")
    })
    public ResponseEntity<Page<BienDTO>> getAllBiens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creerLe") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID categoryId) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Bien> biens;
        
        if (categoryId != null && search != null) {
            biens = bienService.findVisibleBiensByCategoryAndSearch(categoryId, search, pageable);
        } else if (categoryId != null) {
            biens = bienService.findVisibleBiensByCategory(categoryId, pageable);
        } else if (search != null) {
            biens = bienService.findVisibleBiensWithFilters(search, pageable);
        } else {
            biens = bienService.findVisibleBiens(pageable);
        }
        
        Page<BienDTO> bienDTOs = biens.map(BienDTO::fromEntity);
        return ResponseEntity.ok(bienDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupère un bien par ID", description = "Retourne les détails d'un bien spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bien trouvé"),
        @ApiResponse(responseCode = "404", description = "Bien non trouvé")
    })
    public ResponseEntity<BienDTO> getBienById(@PathVariable @Parameter(description = "ID du bien") UUID id) {
        Bien bien = bienService.findById(id);
        return ResponseEntity.ok(BienDTO.fromEntity(bien));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crée un nouveau bien", description = "Crée un bien municipal (ADMIN uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Bien créé avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<BienDTO> createBien(@Valid @RequestBody BienDTO bienDTO) {
        CatBien catBien = null;
        if (bienDTO.getId_cat_bien() != null) {
            catBien = catBienService.findById(bienDTO.getId_cat_bien());
        }
        
        Bien bien = Bien.builder()
                .nom(bienDTO.getNom())
                .description(bienDTO.getDescription())
                .estVisible(bienDTO.getEstVisible() != null ? bienDTO.getEstVisible() : true)
                .catBien(catBien)
                .build();
        
        Bien createdBien = bienService.create(bien);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BienDTO.fromEntity(createdBien));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Met à jour un bien", description = "Modifie les informations d'un bien existant (ADMIN uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bien mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Bien non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<BienDTO> updateBien(@PathVariable @Parameter(description = "ID du bien") UUID id, @Valid @RequestBody BienDTO bienDTO) {
        CatBien catBien = null;
        if (bienDTO.getId_cat_bien() != null) {
            catBien = catBienService.findById(bienDTO.getId_cat_bien());
        }
        
        Bien bien = Bien.builder()
                .nom(bienDTO.getNom())
                .description(bienDTO.getDescription())
                .estVisible(bienDTO.getEstVisible())
                .catBien(catBien)
                .build();
        
        Bien updatedBien = bienService.update(id, bien);
        return ResponseEntity.ok(BienDTO.fromEntity(updatedBien));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprime un bien", description = "Supprime définitivement un bien (ADMIN uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Bien supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Bien non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteBien(@PathVariable @Parameter(description = "ID du bien") UUID id) {
        bienService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/soft-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Masque un bien", description = "Rend un bien invisible sans le supprimer (ADMIN uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bien masqué avec succès"),
        @ApiResponse(responseCode = "404", description = "Bien non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> softDeleteBien(@PathVariable @Parameter(description = "ID du bien") UUID id) {
        bienService.softDelete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/reservations")
    @Operation(summary = "Liste les réservations d'un bien", description = "Récupère toutes les réservations associées à un bien")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des réservations récupérée"),
        @ApiResponse(responseCode = "404", description = "Bien non trouvé")
    })
    public ResponseEntity<List<ReservationDTO>> getReservationsByBien(@PathVariable @Parameter(description = "ID du bien") UUID id) {
        bienService.findById(id);
        List<Reservation> reservations = reservationService.findByBien(id);
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservationDTOs);
    }
}
