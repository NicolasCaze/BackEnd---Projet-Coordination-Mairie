package com.app.controller;

import com.app.dto.BienDTO;
import com.app.dto.ReservationDTO;
import com.app.entity.Bien;
import com.app.entity.CatBien;
import com.app.entity.Reservation;
import com.app.service.BienService;
import com.app.service.CatBienService;
import com.app.service.ReservationService;
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
public class BienController {

    private final BienService bienService;
    private final CatBienService catBienService;
    private final ReservationService reservationService;

    @GetMapping
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
    public ResponseEntity<BienDTO> getBienById(@PathVariable UUID id) {
        Bien bien = bienService.findById(id);
        return ResponseEntity.ok(BienDTO.fromEntity(bien));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<BienDTO> updateBien(@PathVariable UUID id, @Valid @RequestBody BienDTO bienDTO) {
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
    public ResponseEntity<Void> deleteBien(@PathVariable UUID id) {
        bienService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/soft-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteBien(@PathVariable UUID id) {
        bienService.softDelete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<List<ReservationDTO>> getReservationsByBien(@PathVariable UUID id) {
        bienService.findById(id);
        List<Reservation> reservations = reservationService.findByBien(id);
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservationDTOs);
    }
}
