package com.app.service;

import com.app.entity.Delegation;
import com.app.entity.User;
import com.app.repository.DelegationRepository;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DelegationService {

    private final DelegationRepository delegationRepository;
    private final UserRepository userRepository;

    public Delegation createDelegation(UUID fromUserId, UUID toUserId, Delegation.Permission permission) {
        // Vérifier que les utilisateurs existent et sont des admins
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur source non trouvé: " + fromUserId));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur cible non trouvé: " + toUserId));

        if (fromUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Seul un admin peut déléguer des permissions");
        }
        if (toUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Les permissions ne peuvent être déléguées qu'à un admin");
        }

        // Vérifier que la délégation n'existe pas déjà
        Delegation existingDelegation = delegationRepository
                .findActiveDelegationByUserIdAndPermission(toUserId, permission);
        if (existingDelegation != null) {
            throw new RuntimeException("Cette permission est déjà déléguée à cet utilisateur");
        }

        Delegation delegation = Delegation.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .permission(permission)
                .active(true)
                .build();

        return delegationRepository.save(delegation);
    }

    public List<Delegation> getDelegationsReceivedByUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + userId));
        
        return delegationRepository.findActiveDelegationsByUserId(userId);
    }

    public void revokeDelegation(UUID delegationId) {
        Delegation delegation = delegationRepository.findById(delegationId)
                .orElseThrow(() -> new RuntimeException("Délégation non trouvée: " + delegationId));
        
        delegation.setActive(false);
        delegation.setRevokedAt(LocalDateTime.now());
        delegationRepository.save(delegation);
    }

    public boolean hasDelegatedPermission(UUID userId, Delegation.Permission permission) {
        Delegation delegation = delegationRepository
                .findActiveDelegationByUserIdAndPermission(userId, permission);
        return delegation != null && delegation.getActive();
    }

    public List<Delegation> getDelegationsGivenByUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + userId));
        
        return delegationRepository.findDelegationsByFromUserId(userId);
    }
}
