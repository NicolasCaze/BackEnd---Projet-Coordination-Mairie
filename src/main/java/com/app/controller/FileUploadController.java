package com.app.controller;

import com.app.annotation.Audited;
import com.app.entity.PieceJustificative;
import com.app.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/piece-justificative/{reservationId}")
    @Audited(action = "UPLOAD_PIECE_JUSTIFICATIVE")
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
