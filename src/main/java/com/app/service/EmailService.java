package com.app.service;

import com.app.entity.Groupe;
import com.app.entity.Reservation;
import com.app.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

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

    public void sendReservationNotificationToSuperAdmin(Reservation reservation, String superAdminEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(superAdminEmail);
        message.setSubject("Nouvelle réservation en attente de validation");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateDebut = reservation.getDate_debut() != null ? reservation.getDate_debut().format(formatter) : "N/A";
        String dateFin = reservation.getDate_fin() != null ? reservation.getDate_fin().format(formatter) : "N/A";
        
        message.setText(String.format(
            "Bonjour,\n\n" +
            "Une nouvelle réservation a été créée et est en attente de validation.\n\n" +
            "Détails de la réservation :\n" +
            "- Bien : %s\n" +
            "- Demandeur : %s %s (%s)\n" +
            "- Date de début : %s\n" +
            "- Date de fin : %s\n" +
            "- Motif : %s\n" +
            "- Nombre de personnes : %s\n\n" +
            "Veuillez vous connecter au tableau de bord pour valider ou refuser cette réservation.\n\n" +
            "Cordialement,\n" +
            "Système de réservation de la Mairie",
            reservation.getBien() != null ? reservation.getBien().getNom() : "N/A",
            reservation.getUser() != null ? reservation.getUser().getPrenom() : "N/A",
            reservation.getUser() != null ? reservation.getUser().getNom() : "N/A",
            reservation.getUser() != null ? reservation.getUser().getEmail() : "N/A",
            dateDebut,
            dateFin,
            reservation.getMotif() != null ? reservation.getMotif() : "Aucun motif spécifié",
            reservation.getNombrePersonnes() != null ? reservation.getNombrePersonnes().toString() : "N/A"
        ));
        
        mailSender.send(message);
    }

    public void sendGroupeInvitationToAdmin(Groupe groupe, User adminUser) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            
            helper.setFrom(secretariatEmail);
            helper.setTo(adminUser.getEmail());
            helper.setSubject("Vous avez été désigné administrateur d'un groupe");
            
            String emailBody = "Bonjour " + adminUser.getPrenom() + " " + adminUser.getNom() + ",\n\n" +
                "Vous avez été désigné comme administrateur du groupe \"" + groupe.getNom() + "\".\n\n" +
                "En tant qu'administrateur de ce groupe, vous pouvez inviter des membres à rejoindre le groupe " +
                "en leur communiquant le code d'invitation suivant :\n\n" +
                "*** CODE D'INVITATION : " + groupe.getCodeInvitation() + " ***\n\n" +
                "Les utilisateurs pourront utiliser ce code pour rejoindre votre groupe.\n\n" +
                "Vous pouvez gérer les membres de votre groupe depuis votre tableau de bord.\n\n" +
                "Cordialement,\n" +
                "Système de réservation de la Mairie";
            
            helper.setText(emailBody);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
}
