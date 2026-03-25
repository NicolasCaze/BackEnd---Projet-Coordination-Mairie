package com.app.controller;

import com.app.dto.ReservationDTO;
import com.app.entity.Groupe;
import com.app.entity.Reservation;
import com.app.service.GroupeService;
import com.app.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groupes")
@RequiredArgsConstructor
public class GroupeController {

    private final GroupeService groupeService;
    private final ReservationService reservationService;

    @GetMapping("/{id}/reservations")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<List<ReservationDTO>> getGroupeReservations(@PathVariable UUID id) {
        groupeService.findById(id);
        List<Reservation> reservations = reservationService.findByGroupe(id);
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservationDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<Groupe> getGroupeById(@PathVariable UUID id) {
        Groupe groupe = groupeService.findById(id);
        return ResponseEntity.ok(groupe);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<List<Groupe>> getAllGroupes() {
        List<Groupe> groupes = groupeService.findAll();
        return ResponseEntity.ok(groupes);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<Groupe> createGroupe(@RequestBody Groupe groupe) {
        Groupe createdGroupe = groupeService.create(groupe);
        return ResponseEntity.status(201).body(createdGroupe);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<Groupe> updateGroupe(@PathVariable UUID id, @RequestBody Groupe groupe) {
        Groupe updatedGroupe = groupeService.update(id, groupe);
        return ResponseEntity.ok(updatedGroupe);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGroupe(@PathVariable UUID id) {
        groupeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
