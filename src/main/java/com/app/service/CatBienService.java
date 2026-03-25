package com.app.service;

import com.app.entity.CatBien;
import com.app.repository.CatBienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatBienService {

    private final CatBienRepository catBienRepository;

    public List<CatBien> findAll() {
        return catBienRepository.findAll();
    }

    public CatBien findById(UUID id) {
        return catBienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie de bien non trouvée : " + id));
    }

    public CatBien create(CatBien catBien) {
        return catBienRepository.save(catBien);
    }

    public CatBien update(UUID id, CatBien updated) {
        CatBien catBien = findById(id);
        catBien.setNom(updated.getNom());
        catBien.setDescription(updated.getDescription());
        return catBienRepository.save(catBien);
    }

    public void delete(UUID id) {
        findById(id);
        catBienRepository.deleteById(id);
    }
}
