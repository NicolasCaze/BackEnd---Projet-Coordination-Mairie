// ===== UserController.java =====
package com.app.controller;

import com.app.entity.User;
import com.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.status(201).body(userService.create(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable UUID id, @RequestBody User user) {
        return ResponseEntity.ok(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


// ===== GroupeController.java =====
package com.app.controller;

import com.app.entity.Groupe;
import com.app.entity.UserGroupe;
import com.app.service.GroupeService;
import com.app.service.UserGroupeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groupes")
@RequiredArgsConstructor
public class GroupeController {

    private final GroupeService groupeService;
    private final UserGroupeService userGroupeService;

    @GetMapping
    public ResponseEntity<List<Groupe>> getAll() {
        return ResponseEntity.ok(groupeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Groupe> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(groupeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Groupe> create(@RequestBody Groupe groupe) {
        return ResponseEntity.status(201).body(groupeService.create(groupe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Groupe> update(@PathVariable UUID id, @RequestBody Groupe groupe) {
        return ResponseEntity.ok(groupeService.update(id, groupe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        groupeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Membres ---

    @GetMapping("/{id}/membres")
    public ResponseEntity<List<UserGroupe>> getMembres(@PathVariable UUID id) {
        return ResponseEntity.ok(userGroupeService.findMembresByGroupe(id));
    }

    @PostMapping("/{id}/membres/{userId}")
    public ResponseEntity<UserGroupe> addMembre(@PathVariable UUID id, @PathVariable UUID userId) {
        return ResponseEntity.status(201).body(userGroupeService.addMembre(id, userId));
    }

    @PutMapping("/{id}/membres/{userId}")
    public ResponseEntity<UserGroupe> updateMembreStatut(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @RequestParam UserGroupe.Statut statut) {
        return ResponseEntity.ok(userGroupeService.updateStatut(id, userId, statut));
    }

    @DeleteMapping("/{id}/membres/{userId}")
    public ResponseEntity<Void> removeMembre(@PathVariable UUID id, @PathVariable UUID userId) {
        userGroupeService.removeMembre(id, userId);
        return ResponseEntity.noContent().build();
    }

    // --- Réservations du groupe ---

    @GetMapping("/{id}/reservations")
    public ResponseEntity<?> getReservations(
            @PathVariable UUID id,
            com.app.service.ReservationService reservationService) {
        return ResponseEntity.ok(reservationService.findByGroupe(id));
    }
}


// ===== BienController.java =====
package com.app.controller;

import com.app.entity.Bien;
import com.app.entity.Tarif;
import com.app.service.BienService;
import com.app.service.ReservationService;
import com.app.service.TarifService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/biens")
@RequiredArgsConstructor
public class BienController {

    private final BienService bienService;
    private final TarifService tarifService;
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Bien>> getAll() {
        return ResponseEntity.ok(bienService.findAll());
    }

    @GetMapping("/visibles")
    public ResponseEntity<List<Bien>> getAllVisibles() {
        return ResponseEntity.ok(bienService.findAllVisible());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bien> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(bienService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Bien> create(@RequestBody Bien bien) {
        return ResponseEntity.status(201).body(bienService.create(bien));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bien> update(@PathVariable UUID id, @RequestBody Bien bien) {
        return ResponseEntity.ok(bienService.update(id, bien));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bienService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Tarif ---

    @GetMapping("/{id}/tarif")
    public ResponseEntity<Tarif> getTarif(@PathVariable UUID id) {
        return ResponseEntity.ok(tarifService.findByBien(id));
    }

    @PostMapping("/{id}/tarif")
    public ResponseEntity<Tarif> createTarif(@PathVariable UUID id, @RequestBody Tarif tarif) {
        return ResponseEntity.status(201).body(tarifService.create(id, tarif));
    }

    @PutMapping("/{id}/tarif")
    public ResponseEntity<Tarif> updateTarif(@PathVariable UUID id, @RequestBody Tarif tarif) {
        return ResponseEntity.ok(tarifService.update(id, tarif));
    }

    @DeleteMapping("/{id}/tarif")
    public ResponseEntity<Void> deleteTarif(@PathVariable UUID id) {
        tarifService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Réservations du bien ---

    @GetMapping("/{id}/reservations")
    public ResponseEntity<?> getReservations(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.findByBien(id));
    }
}


// ===== ReservationController.java =====
package com.app.controller;

import com.app.entity.Reservation;
import com.app.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservation>> getAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody Reservation reservation) {
        return ResponseEntity.status(201).body(reservationService.create(reservation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> update(@PathVariable UUID id, @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.update(id, reservation));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Reservation> updateStatut(
            @PathVariable UUID id,
            @RequestParam Reservation.Statut statut) {
        return ResponseEntity.ok(reservationService.updateStatut(id, statut));
    }

    @PatchMapping("/{id}/caution")
    public ResponseEntity<Reservation> updateCaution(
            @PathVariable UUID id,
            @RequestParam Reservation.StatutCaution statutCaution) {
        return ResponseEntity.ok(reservationService.updateStatutCaution(id, statutCaution));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
