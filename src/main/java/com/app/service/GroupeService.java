package com.app.service;

import com.app.entity.Groupe;
import com.app.repository.GroupeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class GroupeService {

    private final GroupeRepository groupeRepository;
    
    public GroupeService(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }

    public Page<Groupe> findAll(Pageable pageable) {
        return groupeRepository.findAll(pageable);
    }
    
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
        groupe.setType_exoneration(updated.getType_exoneration());
        groupe.setNiveau_tarif(updated.getNiveau_tarif());
        return groupeRepository.save(groupe);
    }

    public void delete(UUID id) {
        Groupe groupe = findById(id);
        
        if (groupe.getType_groupe() == Groupe.TypeGroupe.CONSEIL_MUNICIPAL) {
            throw new RuntimeException("Le groupe CONSEIL_MUNICIPAL ne peut pas être supprimé");
        }
        
        groupeRepository.deleteById(id);
    }
}
