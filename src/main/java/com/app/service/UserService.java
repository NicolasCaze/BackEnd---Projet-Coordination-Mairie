package com.app.service;

import com.app.entity.User;
import com.app.exception.LastAdminException;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User non trouvé : " + id));
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public User update(UUID id, User updated) {
        User user = findById(id);
        
        if (user.getRole() == User.Role.ADMIN && updated.getRole() != User.Role.ADMIN) {
            long adminCount = userRepository.countByRole(User.Role.ADMIN);
            if (adminCount <= 1) {
                throw new LastAdminException("cannot remove last admin");
            }
        }
        
        user.setNom(updated.getNom());
        user.setPrenom(updated.getPrenom());
        user.setEmail(updated.getEmail());
        user.setTelephone(updated.getTelephone());
        user.setIs_resident(updated.getIs_resident());
        user.setIs_tutored(updated.getIs_tutored());
        user.setNiveau_tarif(updated.getNiveau_tarif());
        user.setRole(updated.getRole());
        user.setStatut(updated.getStatut());
        return userRepository.save(user);
    }

    public User updateWithoutRoleChange(UUID id, User updated, UUID currentUserId) {
        User user = findById(id);
        user.setNom(updated.getNom());
        user.setPrenom(updated.getPrenom());
        user.setEmail(updated.getEmail());
        user.setTelephone(updated.getTelephone());
        user.setIs_resident(updated.getIs_resident());
        user.setIs_tutored(updated.getIs_tutored());
        user.setNiveau_tarif(updated.getNiveau_tarif());
        user.setStatut(updated.getStatut());
        
        if (!id.equals(currentUserId)) {
            if (user.getRole() == User.Role.ADMIN && updated.getRole() != User.Role.ADMIN) {
                long adminCount = userRepository.countByRole(User.Role.ADMIN);
                if (adminCount <= 1) {
                    throw new LastAdminException("cannot remove last admin");
                }
            }
            user.setRole(updated.getRole());
        }
        
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User non trouvé avec l'email : " + email));
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public void delete(UUID id) {
        User user = findById(id);
        
        if (user.getRole() == User.Role.ADMIN) {
            long adminCount = userRepository.countByRole(User.Role.ADMIN);
            if (adminCount <= 1) {
                throw new LastAdminException("cannot remove last admin");
            }
        }
        
        userRepository.deleteById(id);
    }

    public User updateStatut(UUID id, User.Statut newStatut) {
        User user = findById(id);
        
        if (user.getStatut() != User.Statut.PENDING) {
            throw new RuntimeException("Seuls les comptes en statut PENDING peuvent être validés");
        }
        
        if (newStatut != User.Statut.ACTIVE && newStatut != User.Statut.REJECTED) {
            throw new RuntimeException("Le nouveau statut doit être ACTIVE ou REJECTED");
        }
        
        user.setStatut(newStatut);
        return userRepository.save(user);
    }
}
