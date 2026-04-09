package com.app.service;

import com.app.entity.Bien;
import com.app.entity.Tarif;
import com.app.repository.BienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return bienRepository.findByEstVisibleTrue();
    }

    public Page<Bien> findVisibleBiens(Pageable pageable) {
        return bienRepository.findVisibleBiens(pageable);
    }

    public Page<Bien> findVisibleBiensWithFilters(String search, Pageable pageable) {
        return bienRepository.findVisibleBiensWithFilters(search, pageable);
    }

    public Page<Bien> findVisibleBiensByCategory(UUID categoryId, Pageable pageable) {
        return bienRepository.findVisibleBiensByCategory(categoryId, pageable);
    }

    public Page<Bien> findVisibleBiensByCategoryAndSearch(UUID categoryId, String search, Pageable pageable) {
        return bienRepository.findVisibleBiensByCategoryAndSearch(categoryId, search, pageable);
    }

    public Bien findById(UUID id) {
        return bienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé : " + id));
    }

    public Bien create(Bien bien) {
        // Si un tarif est fourni, l'associer au bien
        if (bien.getTarif() != null) {
            Tarif tarif = bien.getTarif();
            tarif.setBien(bien);
        }
        return bienRepository.save(bien);
    }

    public Bien update(UUID id, Bien updated) {
        Bien bien = findById(id);
        bien.setNom(updated.getNom());
        bien.setDescription(updated.getDescription());
        bien.setEstVisible(updated.getEstVisible());
        bien.setCatBien(updated.getCatBien());
        
        // Mettre à jour le tarif si fourni
        if (updated.getTarif() != null) {
            if (bien.getTarif() != null) {
                // Mettre à jour le tarif existant
                bien.getTarif().setNiveau_1(updated.getTarif().getNiveau_1());
                bien.getTarif().setNiveau_2(updated.getTarif().getNiveau_2());
                bien.getTarif().setNiveau_3(updated.getTarif().getNiveau_3());
                bien.getTarif().setNiveau_4(updated.getTarif().getNiveau_4());
                bien.getTarif().setNiveau_5(updated.getTarif().getNiveau_5());
            } else {
                // Créer un nouveau tarif
                Tarif tarif = updated.getTarif();
                tarif.setBien(bien);
                bien.setTarif(tarif);
            }
        }
        
        return bienRepository.save(bien);
    }

    public void softDelete(UUID id) {
        Bien bien = findById(id);
        bien.setEstVisible(false);
        bienRepository.save(bien);
    }

    public void delete(UUID id) {
        findById(id);
        bienRepository.deleteById(id);
    }
}
