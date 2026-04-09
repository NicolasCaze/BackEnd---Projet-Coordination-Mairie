-- Script pour ajouter des groupes de test sans supprimer les données existantes

-- Vérifier et insérer les groupes s'ils n'existent pas déjà
INSERT INTO groupes (id_groupe, nom, description, type_groupe, type_exoneration, niveau_tarif, creer_le) 
VALUES ('11111111-1111-1111-1111-111111111111'::uuid, 'Association Sportive', 'Association sportive locale', 'ASSOCIATION', 'ASSO', 2, NOW())
ON CONFLICT (id_groupe) DO NOTHING;

INSERT INTO groupes (id_groupe, nom, description, type_groupe, type_exoneration, niveau_tarif, creer_le) 
VALUES ('22222222-2222-2222-2222-222222222222'::uuid, 'Club des Seniors', 'Club pour les personnes âgées', 'ASSOCIATION', 'ASSO', 3, NOW())
ON CONFLICT (id_groupe) DO NOTHING;

INSERT INTO groupes (id_groupe, nom, description, type_groupe, type_exoneration, niveau_tarif, creer_le) 
VALUES ('33333333-3333-3333-3333-333333333333'::uuid, 'Comité des Fêtes', 'Organisation des événements municipaux', 'ASSOCIATION', 'ASSO', 2, NOW())
ON CONFLICT (id_groupe) DO NOTHING;

INSERT INTO groupes (id_groupe, nom, description, type_groupe, type_exoneration, niveau_tarif, creer_le) 
VALUES ('44444444-4444-4444-4444-444444444444'::uuid, 'Club de Lecture', 'Passionnés de littérature', 'ASSOCIATION', 'ASSO', 3, NOW())
ON CONFLICT (id_groupe) DO NOTHING;

-- Ajouter les membres aux groupes (admin@mairie.fr = 11111111-1111-1111-1111-111111111111)
INSERT INTO user_groupe (id_user, id_groupe, status, rejoint_le) 
VALUES ('11111111-1111-1111-1111-111111111111'::uuid, '11111111-1111-1111-1111-111111111111'::uuid, 'ACTIF', NOW())
ON CONFLICT (id_user, id_groupe) DO NOTHING;

INSERT INTO user_groupe (id_user, id_groupe, status, rejoint_le) 
VALUES ('11111111-1111-1111-1111-111111111111'::uuid, '22222222-2222-2222-2222-222222222222'::uuid, 'ACTIF', NOW())
ON CONFLICT (id_user, id_groupe) DO NOTHING;

INSERT INTO user_groupe (id_user, id_groupe, status, rejoint_le) 
VALUES ('11111111-1111-1111-1111-111111111111'::uuid, '33333333-3333-3333-3333-333333333333'::uuid, 'ACTIF', NOW())
ON CONFLICT (id_user, id_groupe) DO NOTHING;

-- Ajouter Jean Dupont aux groupes
INSERT INTO user_groupe (id_user, id_groupe, status, rejoint_le) 
VALUES ('22222222-2222-2222-2222-222222222222'::uuid, '11111111-1111-1111-1111-111111111111'::uuid, 'ACTIF', NOW())
ON CONFLICT (id_user, id_groupe) DO NOTHING;

INSERT INTO user_groupe (id_user, id_groupe, status, rejoint_le) 
VALUES ('22222222-2222-2222-2222-222222222222'::uuid, '33333333-3333-3333-3333-333333333333'::uuid, 'ACTIF', NOW())
ON CONFLICT (id_user, id_groupe) DO NOTHING;

-- Ajouter Marie Martin aux groupes
INSERT INTO user_groupe (id_user, id_groupe, status, rejoint_le) 
VALUES ('33333333-3333-3333-3333-333333333333'::uuid, '22222222-2222-2222-2222-222222222222'::uuid, 'ACTIF', NOW())
ON CONFLICT (id_user, id_groupe) DO NOTHING;

INSERT INTO user_groupe (id_user, id_groupe, status, rejoint_le) 
VALUES ('33333333-3333-3333-3333-333333333333'::uuid, '44444444-4444-4444-4444-444444444444'::uuid, 'ACTIF', NOW())
ON CONFLICT (id_user, id_groupe) DO NOTHING;
