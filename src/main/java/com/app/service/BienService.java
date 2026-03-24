package com.app.service;

import com.app.entity.Bien;
import com.app.repository.BienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BienService {

    private final BienRepository bienRepository;

    public List<Bien> findAll() {
        return bienRepository.findAll();
    }

    public List<Bien> findAllVisible() {
        return bienRepository.findByEstVisible(true);
    }

    public Bien findById(UUID id) {
        return bienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé : " + id));
    }

    public Bien create(Bien bien) {
        return bienRepository.save(bien);
    }

    public Bien update(UUID id, Bien updated) {
        Bien bien = findById(id);
        bien.setNom(updated.getNom());
        bien.setDescription(updated.getDescription());
        bien.setEst_visible(updated.getEst_visible());
        bien.setCatBien(updated.getCatBien());
        return bienRepository.save(bien);
    }

    public void delete(UUID id) {
        findById(id);
        bienRepository.deleteById(id);
    }
}
