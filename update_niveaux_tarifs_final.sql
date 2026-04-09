-- Mise à jour des niveaux tarifaires selon la nouvelle structure
-- Niveau 5 : Conseil Municipal
-- Niveau 4 : Hors-Village
-- Niveau 3 : Village
-- Niveau 2 : Association
-- Niveau 1 : Membre individuel (utilisateurs)

-- Mettre à jour les groupes selon leur type
UPDATE groupes SET niveau_tarif = 2 WHERE type_groupe = 'ASSOCIATION';
UPDATE groupes SET niveau_tarif = 3 WHERE type_groupe = 'VILLAGE';
UPDATE groupes SET niveau_tarif = 4 WHERE type_groupe = 'HORS_VILLAGE';
UPDATE groupes SET niveau_tarif = 5 WHERE type_groupe = 'CONSEIL_MUNICIPAL';

-- Assigner niveau 1 à tous les utilisateurs individuels
UPDATE users SET niveau_tarif = 1 WHERE niveau_tarif IS NULL OR niveau_tarif = 0;

-- Vérifier les résultats
SELECT 'Groupes mis à jour :' as info;
SELECT type_groupe, COUNT(*) as nombre, niveau_tarif 
FROM groupes 
GROUP BY type_groupe, niveau_tarif 
ORDER BY niveau_tarif;

SELECT 'Utilisateurs mis à jour :' as info;
SELECT COUNT(*) as nombre_utilisateurs, niveau_tarif 
FROM users 
GROUP BY niveau_tarif 
ORDER BY niveau_tarif;
