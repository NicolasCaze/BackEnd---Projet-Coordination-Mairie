package com.app.service;

import com.app.entity.UserGroupe;
import com.app.entity.UserGroupeId;
import com.app.repository.UserGroupeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserGroupeService {

    private final UserGroupeRepository userGroupeRepository;
    private final UserService userService;
    private final GroupeService groupeService;
    
    public UserGroupeService(UserGroupeRepository userGroupeRepository, UserService userService, GroupeService groupeService) {
        this.userGroupeRepository = userGroupeRepository;
        this.userService = userService;
        this.groupeService = groupeService;
    }

    public Page<UserGroupe> findMembresByGroupe(UUID id_groupe, Pageable pageable) {
        groupeService.findById(id_groupe);
        return userGroupeRepository.findByGroupeIdGroupe(id_groupe, pageable);
    }
    
    public List<UserGroupe> findMembresByGroupe(UUID id_groupe) {
        groupeService.findById(id_groupe);
        return userGroupeRepository.findByGroupeIdGroupe(id_groupe);
    }

    public List<UserGroupe> findGroupesByUser(UUID id_user) {
        userService.findById(id_user);
        return userGroupeRepository.findByUserIdUser(id_user);
    }

    public UserGroupe addMembre(UUID id_groupe, UUID id_user) {
        userService.findById(id_user);
        groupeService.findById(id_groupe);

        UserGroupeId compositeId = new UserGroupeId(id_user, id_groupe);
        if (userGroupeRepository.existsById(compositeId)) {
            throw new RuntimeException("L'utilisateur est déjà membre de ce groupe");
        }

        UserGroupe userGroupe = UserGroupe.builder()
                .id(compositeId)
                .user(userService.findById(id_user))
                .groupe(groupeService.findById(id_groupe))
                .status(UserGroupe.Status.ACTIF)
                .build();
        return userGroupeRepository.save(userGroupe);
    }

    public UserGroupe updateStatut(UUID id_groupe, UUID id_user, UserGroupe.Status status) {
        UserGroupeId compositeId = new UserGroupeId(id_user, id_groupe);
        UserGroupe userGroupe = userGroupeRepository.findById(compositeId)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé dans ce groupe"));
        userGroupe.setStatus(status);
        return userGroupeRepository.save(userGroupe);
    }

    public void removeMembre(UUID id_groupe, UUID id_user) {
        UserGroupeId compositeId = new UserGroupeId(id_user, id_groupe);
        if (!userGroupeRepository.existsById(compositeId)) {
            throw new RuntimeException("Membre non trouvé dans ce groupe");
        }
        userGroupeRepository.deleteById(compositeId);
    }

    public boolean isUserActiveMember(UUID id_user, UUID id_groupe) {
        UserGroupeId compositeId = new UserGroupeId(id_user, id_groupe);
        return userGroupeRepository.findById(compositeId)
                .map(userGroupe -> userGroupe.getStatus() == UserGroupe.Status.ACTIF)
                .orElse(false);
    }
}
