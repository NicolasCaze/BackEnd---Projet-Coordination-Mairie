package com.app.service;

import com.app.entity.Groupe;
import com.app.entity.User;
import com.app.entity.UserGroupe;
import com.app.repository.GroupeRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GroupeService {

    private final GroupeRepository groupeRepository;
    private final UserService userService;
    private final UserGroupeService userGroupeService;
    
    public GroupeService(GroupeRepository groupeRepository, UserService userService, @Lazy UserGroupeService userGroupeService) {
        this.groupeRepository = groupeRepository;
        this.userService = userService;
        this.userGroupeService = userGroupeService;
    }

    public Page<Groupe> findAll(Pageable pageable) {
        return groupeRepository.findAll(pageable);
    }
    
    public List<Groupe> findAll() {
        return groupeRepository.findAll();
    }

    public Groupe findById(UUID id) {
        return groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé : " + id));
    }

    public Groupe create(Groupe groupe) {
        // Générer un code d'invitation unique
        groupe.setCodeInvitation(generateInvitationCode());
        return groupeRepository.save(groupe);
    }

    private String generateInvitationCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(6);
        
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        // Vérifier l'unicité du code
        String generatedCode = code.toString();
        while (groupeRepository.existsByCodeInvitation(generatedCode)) {
            code = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                code.append(characters.charAt(random.nextInt(characters.length())));
            }
            generatedCode = code.toString();
        }
        
        return generatedCode;
    }

    public Groupe update(UUID id, Groupe updated) {
        Groupe groupe = findById(id);
        groupe.setNom(updated.getNom());
        groupe.setDescription(updated.getDescription());
        groupe.setType_groupe(updated.getType_groupe());
        groupe.setType_exoneration(updated.getType_exoneration());
        groupe.setNiveau_tarif(updated.getNiveau_tarif());
        return groupeRepository.save(groupe);
    }

    public void delete(UUID id) {
        Groupe groupe = findById(id);
        
        if (groupe.getType_groupe() == Groupe.TypeGroupe.CONSEIL_MUNICIPAL) {
            throw new RuntimeException("Le groupe CONSEIL_MUNICIPAL ne peut pas être supprimé");
        }
        
        // Supprimer d'abord tous les membres du groupe
        List<UserGroupe> membres = userGroupeService.findMembresByGroupe(id);
        for (UserGroupe membre : membres) {
            userGroupeService.removeMembre(id, membre.getUser().getId_user());
        }
        
        groupeRepository.deleteById(id);
    }

    public List<Groupe> findGroupesByUserEmail(String email) {
        User user = userService.findByEmail(email);
        List<UserGroupe> userGroupes = userGroupeService.findGroupesByUser(user.getId_user());
        return userGroupes.stream()
                .map(UserGroupe::getGroupe)
                .collect(Collectors.toList());
    }
    
    public Groupe findByCodeInvitation(String codeInvitation) {
        return groupeRepository.findByCodeInvitation(codeInvitation);
    }
}
