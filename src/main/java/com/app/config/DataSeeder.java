package com.app.config;

import com.app.entity.Groupe;
import com.app.repository.GroupeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final GroupeRepository groupeRepository;

    @Override
    public void run(String... args) throws Exception {
        seedConseilMunicipal();
    }

    private void seedConseilMunicipal() {
        long count = groupeRepository.findAll().stream()
                .filter(g -> g.getType_groupe() == Groupe.TypeGroupe.CONSEIL_MUNICIPAL)
                .count();

        if (count == 0) {
            Groupe conseilMunicipal = Groupe.builder()
                    .nom("Conseil Municipal")
                    .description("Groupe spécial réservé aux membres du conseil municipal")
                    .type_groupe(Groupe.TypeGroupe.CONSEIL_MUNICIPAL)
                    .type_exoneration(Groupe.TypeExoneration.MANDAT_ELECTIF)
                    .niveau_tarif(1)
                    .build();

            groupeRepository.save(conseilMunicipal);
            System.out.println("✓ Groupe CONSEIL_MUNICIPAL créé avec succès");
        } else {
            System.out.println("✓ Groupe CONSEIL_MUNICIPAL déjà existant");
        }
    }
}
