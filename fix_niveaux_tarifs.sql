-- Vérifier les niveaux tarifaires actuels des groupes
SELECT id_groupe, nom, type_groupe, niveau_tarif 
FROM groupes 
ORDER BY type_groupe, nom;

-- Mettre à jour les niveaux tarifaires selon le type de groupe
UPDATE groupes SET niveau_tarif = 2 WHERE type_groupe = 'ASSOCIATION';
UPDATE groupes SET niveau_tarif = 3 WHERE type_groupe = 'PARTICULIER';
UPDATE groupes SET niveau_tarif = 4 WHERE type_groupe = 'ENTREPRISE';
UPDATE groupes SET niveau_tarif = 5 WHERE type_groupe = 'CONSEIL_MUNICIPAL';

-- Assigner niveau 1 par défaut à tous les utilisateurs
UPDATE users SET niveau_tarif = 1 WHERE niveau_tarif IS NULL;

-- Vérifier les résultats après mise à jour
SELECT id_groupe, nom, type_groupe, niveau_tarif 
FROM groupes 
ORDER BY type_groupe, nom;

SELECT 'Mise à jour terminée' as status;
