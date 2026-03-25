package com.app.service;

import com.app.entity.Delegation;
import com.app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DelegationPermissionService {

    private final DelegationService delegationService;

    public boolean hasPermissionOrDelegatedPermission(Authentication authentication, Delegation.Permission requiredPermission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        
        // Vérifier si l'utilisateur a le rôle ADMIN
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }

        // Vérifier si l'utilisateur a la permission déléguée
        return delegationService.hasDelegatedPermission(currentUser.getId_user(), requiredPermission);
    }

    public void checkPermissionOrDelegatedPermission(Authentication authentication, Delegation.Permission requiredPermission, String errorMessage) {
        if (!hasPermissionOrDelegatedPermission(authentication, requiredPermission)) {
            throw new RuntimeException(errorMessage);
        }
    }

    public UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        
        User currentUser = (User) authentication.getPrincipal();
        return currentUser.getId_user();
    }
}
