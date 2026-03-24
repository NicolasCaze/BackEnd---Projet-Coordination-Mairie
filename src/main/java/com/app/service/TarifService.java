package com.app.service;

import com.app.entity.Tarif;
import com.app.repository.TarifRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TarifService {

    private final TarifRepository tarifRepository;
    private final BienService bienService;

    public Tarif findByBien(UUID id_bien) {
        bienService.findById(id_bien);
        return tarifRepository.findByBienIdBien(id_bien)
                .orElseThrow(() -> new RuntimeException("Tarif non trouvé pour le bien : " + id_bien));
    }

    public Tarif create(UUID id_bien, Tarif tarif) {
        tarif.setBien(bienService.findById(id_bien));
        if (tarifRepository.findByBienIdBien(id_bien).isPresent()) {
            throw new RuntimeException("Un tarif existe déjà pour ce bien");
        }
        return tarifRepository.save(tarif);
    }

    public Tarif update(UUID id_bien, Tarif updated) {
        Tarif tarif = findByBien(id_bien);
        tarif.setNiveau_1(updated.getNiveau_1());
        tarif.setNiveau_2(updated.getNiveau_2());
        tarif.setNiveau_3(updated.getNiveau_3());
        tarif.setNiveau_4(updated.getNiveau_4());
        tarif.setNiveau_5(updated.getNiveau_5());
        return tarifRepository.save(tarif);
    }

    public void delete(UUID id_bien) {
        Tarif tarif = findByBien(id_bien);
        tarifRepository.delete(tarif);
    }
}
