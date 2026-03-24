package com.app.service;

import com.app.entity.Groupe;
import com.app.repository.GroupeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupeService {

    private final GroupeRepository groupeRepository;

    public List<Groupe> findAll() {
        return groupeRepository.findAll();
    }

    public Groupe findById(UUID id) {
        return groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé : " + id));
    }

    public Groupe create(Groupe groupe) {
        return groupeRepository.save(groupe);
    }

    public Groupe update(UUID id, Groupe updated) {
        Groupe groupe = findById(id);
        groupe.setNom(updated.getNom());
        groupe.setDescription(updated.getDescription());
        groupe.setType_groupe(updated.getType_groupe());
        groupe.setIs_conseil_municipal(updated.getIs_conseil_municipal());
        groupe.setExo_association(updated.getExo_association());
        groupe.setExo_critere_social(updated.getExo_critere_social());
        groupe.setExo_mandat_electif(updated.getExo_mandat_electif());
        groupe.setNiveau_tarif(updated.getNiveau_tarif());
        return groupeRepository.save(groupe);
    }

    public void delete(UUID id) {
        findById(id);
        groupeRepository.deleteById(id);
    }
}
