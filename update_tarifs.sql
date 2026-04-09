-- Script pour mettre à jour les niveaux tarifaires des groupes et utilisateurs

-- Mettre à jour les niveaux tarifaires des groupes selon leur type
UPDATE groupes SET niveau_tarif = 2 WHERE type_groupe = 'ASSOCIATION';
UPDATE groupes SET niveau_tarif = 3 WHERE type_groupe = 'PARTICULIER'; -- Groupe de particuliers
UPDATE groupes SET niveau_tarif = 4 WHERE type_groupe = 'ENTREPRISE';
UPDATE groupes SET niveau_tarif = 5 WHERE type_groupe = 'CONSEIL_MUNICIPAL';

-- Assigner niveau 1 par défaut à tous les utilisateurs qui n'ont pas de niveau
UPDATE users SET niveau_tarif = 1 WHERE niveau_tarif IS NULL;

-- Exemples de tarifs pour un bien (à adapter selon vos besoins)
-- INSERT INTO tarifs (id_bien, niveau_1, niveau_2, niveau_3, niveau_4, niveau_5)
-- VALUES 
--   ('id_salle_fetes', 100.00, 50.00, 60.00, 200.00, 0.00);
--   -- Niveau 1 (Individuel): 100€
--   -- Niveau 2 (Association): 50€ (réduction 50%)
--   -- Niveau 3 (Groupe particuliers): 60€
--   -- Niveau 4 (Entreprise): 200€ (tarif commercial)
--   -- Niveau 5 (Conseil municipal): Gratuit

SELECT 'Niveaux tarifaires mis à jour avec succès' as message;
