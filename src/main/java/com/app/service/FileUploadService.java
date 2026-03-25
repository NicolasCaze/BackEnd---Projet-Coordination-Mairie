package com.app.service;

import com.app.entity.PieceJustificative;
import com.app.entity.Reservation;
import com.app.repository.PieceJustificativeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final ReservationService reservationService;
    private final EmailService emailService;

    @Value("${app.upload.directory}")
    private String uploadDirectory;

    @Value("${app.upload.max-size}")
    private long maxFileSize;

    public PieceJustificative uploadPieceJustificative(UUID reservationId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("Le fichier dépasse la taille maximale autorisée");
        }

        Reservation reservation = reservationService.findById(reservationId);

        File uploadDir = new File(uploadDirectory);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(uploadDirectory, filename);
        Files.write(filepath, file.getBytes());

        PieceJustificative piece = PieceJustificative.builder()
                .reservation(reservation)
                .filename(file.getOriginalFilename())
                .filepath(filepath.toString())
                .build();

        piece = pieceJustificativeRepository.save(piece);

        try {
            emailService.sendPieceJustificativeNotification(
                reservationId.toString(),
                file.getOriginalFilename()
            );
            
            piece.setSent_at(LocalDateTime.now());
            piece = pieceJustificativeRepository.save(piece);

            deleteFileAfterSend(piece);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }

        return piece;
    }

    private void deleteFileAfterSend(PieceJustificative piece) {
        try {
            Path path = Paths.get(piece.getFilepath());
            Files.deleteIfExists(path);
            
            piece.setDeleted_at(LocalDateTime.now());
            pieceJustificativeRepository.save(piece);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression du fichier: " + e.getMessage());
        }
    }
}
