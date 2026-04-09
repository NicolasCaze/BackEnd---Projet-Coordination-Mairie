package com.app.service;

import com.app.entity.TypeGroupe;
import com.app.repository.TypeGroupeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TypeGroupeService {
    
    private final TypeGroupeRepository typeGroupeRepository;
    
    public List<TypeGroupe> findAll() {
        return typeGroupeRepository.findAll();
    }
    
    public TypeGroupe findById(UUID id) {
        return typeGroupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type de groupe non trouvé"));
    }
    
    @Transactional
    public TypeGroupe create(TypeGroupe typeGroupe) {
        if (typeGroupeRepository.existsByNom(typeGroupe.getNom())) {
            throw new RuntimeException("Un type de groupe avec ce nom existe déjà");
        }
        return typeGroupeRepository.save(typeGroupe);
    }
    
    @Transactional
    public TypeGroupe update(UUID id, TypeGroupe typeGroupe) {
        TypeGroupe existing = findById(id);
        
        if (!existing.getNom().equals(typeGroupe.getNom()) && 
            typeGroupeRepository.existsByNom(typeGroupe.getNom())) {
            throw new RuntimeException("Un type de groupe avec ce nom existe déjà");
        }
        
        existing.setNom(typeGroupe.getNom());
        existing.setDescription(typeGroupe.getDescription());
        
        return typeGroupeRepository.save(existing);
    }
    
    @Transactional
    public void delete(UUID id) {
        TypeGroupe typeGroupe = findById(id);
        typeGroupeRepository.delete(typeGroupe);
    }
}
