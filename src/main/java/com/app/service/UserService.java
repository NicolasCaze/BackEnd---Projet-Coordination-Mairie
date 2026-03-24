package com.app.service;

import com.app.entity.User;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
        user.setNom(updated.getNom());
        user.setPrenom(updated.getPrenom());
        user.setEmail(updated.getEmail());
        user.setTelephone(updated.getTelephone());
        user.setIs_resident(updated.getIs_resident());
        user.setRole(updated.getRole());
        user.setStatut(updated.getStatut());
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User non trouvé avec l'email : " + email));
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        // Pour l'instant, comparaison simple. À remplacer par BCrypt plus tard
        return rawPassword.equals(encodedPassword);
    }

    public void delete(UUID id) {
        findById(id);
        userRepository.deleteById(id);
    }
}
