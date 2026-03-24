package com.app.service;

import com.app.entity.UserGroupe;
import com.app.entity.UserGroupeId;
import com.app.repository.UserGroupeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserGroupeService {

    private final UserGroupeRepository userGroupeRepository;
    private final UserService userService;
    private final GroupeService groupeService;

    public List<UserGroupe> findMembresByGroupe(UUID id_groupe) {
        groupeService.findById(id_groupe); // vérifie que le groupe existe
        return userGroupeRepository.findByIdGroupe(id_groupe);
    }

    public List<UserGroupe> findGroupesByUser(UUID id_user) {
        userService.findById(id_user); // vérifie que le user existe
        return userGroupeRepository.findByIdUser(id_user);
    }

    public UserGroupe addMembre(UUID id_groupe, UUID id_user) {
        userService.findById(id_user);
        groupeService.findById(id_groupe);

        UserGroupeId compositeId = new UserGroupeId(id_user, id_groupe);
        if (userGroupeRepository.existsById(compositeId)) {
            throw new RuntimeException("L'utilisateur est déjà membre de ce groupe");
        }

        UserGroupe userGroupe = UserGroupe.builder()
                .id_user(id_user)
                .id_groupe(id_groupe)
                .statut(UserGroupe.Statut.EN_ATTENTE)
                .build();
        return userGroupeRepository.save(userGroupe);
    }

    public UserGroupe updateStatut(UUID id_groupe, UUID id_user, UserGroupe.Statut statut) {
        UserGroupeId compositeId = new UserGroupeId(id_user, id_groupe);
        UserGroupe userGroupe = userGroupeRepository.findById(compositeId)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé dans ce groupe"));
        userGroupe.setStatut(statut);
        return userGroupeRepository.save(userGroupe);
    }

    public void removeMembre(UUID id_groupe, UUID id_user) {
        UserGroupeId compositeId = new UserGroupeId(id_user, id_groupe);
        if (!userGroupeRepository.existsById(compositeId)) {
            throw new RuntimeException("Membre non trouvé dans ce groupe");
        }
        userGroupeRepository.deleteById(compositeId);
    }
}
