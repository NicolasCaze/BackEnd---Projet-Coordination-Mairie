package com.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.secretariat}")
    private String secretariatEmail;

    public void sendPieceJustificativeNotification(String reservationId, String filename) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(secretariatEmail);
        message.setSubject("Nouvelle pièce justificative reçue");
        message.setText(String.format(
            "Une nouvelle pièce justificative a été uploadée.\n\n" +
            "Réservation ID: %s\n" +
            "Fichier: %s\n\n" +
            "Veuillez vérifier le document.",
            reservationId,
            filename
        ));
        
        mailSender.send(message);
    }
}
