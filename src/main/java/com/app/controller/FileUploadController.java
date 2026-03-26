package com.app.controller;

import com.app.annotation.Audited;
import com.app.entity.PieceJustificative;
import com.app.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "Upload", description = "Gestion de l'upload de pièces justificatives")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/piece-justificative/{reservationId}")
    @Audited(action = "UPLOAD_PIECE_JUSTIFICATIVE")
    @Operation(summary = "Upload une pièce justificative", description = "Téléverse un fichier justificatif pour une réservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fichier uploadé avec succès"),
        @ApiResponse(responseCode = "400", description = "Fichier invalide ou réservation non trouvée")
    })
    public ResponseEntity<PieceJustificative> uploadPieceJustificative(
            @PathVariable UUID reservationId,
            @RequestParam("file") MultipartFile file) {
        try {
            PieceJustificative piece = fileUploadService.uploadPieceJustificative(reservationId, file);
            return ResponseEntity.ok(piece);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
