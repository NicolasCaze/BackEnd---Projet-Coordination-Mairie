package com.app.service;

import com.app.entity.User;
import com.app.exception.LastAdminException;
import com.app.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User non trouvé : " + id));
    }
    
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + user.getEmail());
        }
        // Hasher le mot de passe avant de sauvegarder
        if (user.getMot_de_passe() != null && !user.getMot_de_passe().isEmpty()) {
            user.setMot_de_passe(passwordEncoder.encode(user.getMot_de_passe()));
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

    public User updateProfile(UUID id, com.app.dto.UserDTO userDTO) {
        User user = findById(id);
        
        // Mettre à jour uniquement les informations personnelles (pas role ni statut)
        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());
        user.setTelephone(userDTO.getTelephone());
        user.setAdresse(userDTO.getAdresse());
        
        // Ne pas permettre de changer l'email pour éviter les conflits
        // Ne pas permettre de changer le role ou le statut
        
        return userRepository.save(user);
    }
}
