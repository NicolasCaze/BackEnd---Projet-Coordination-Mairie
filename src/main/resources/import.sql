-- Insertion des utilisateurs de test
INSERT INTO "user" (id_user, nom, prenom, email, mot_de_passe, telephone, is_resident, role, statut, created_at) VALUES ('11111111-1111-1111-1111-111111111111', 'Admin', 'Super', 'admin@mairie.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0123456789', true, 'ADMIN', 'ACTIF', NOW());
INSERT INTO "user" (id_user, nom, prenom, email, mot_de_passe, telephone, is_resident, role, statut, created_at) VALUES ('22222222-2222-2222-2222-222222222222', 'Dupont', 'Jean', 'jean.dupont@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0123456788', true, 'USER', 'ACTIF', NOW());
INSERT INTO "user" (id_user, nom, prenom, email, mot_de_passe, telephone, is_resident, role, statut, created_at) VALUES ('33333333-3333-3333-3333-333333333333', 'Martin', 'Marie', 'marie.martin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0123456787', false, 'USER', 'ACTIF', NOW());

-- Insertion des catégories de biens
INSERT INTO cat_bien (id_cat_bien, nom, description) VALUES ('c1111111-1111-1111-1111-111111111111', 'Salles municipales', 'Salles polyvalentes et salles de réunion');
INSERT INTO cat_bien (id_cat_bien, nom, description) VALUES ('c2222222-2222-2222-2222-222222222222', 'Équipements sportifs', 'Gymnases, terrains de sport, piscines');
INSERT INTO cat_bien (id_cat_bien, nom, description) VALUES ('c3333333-3333-3333-3333-333333333333', 'Matériel', 'Matériel et équipements divers');

-- Insertion des biens
INSERT INTO bien (id_bien, id_cat_bien, nom, description, capacite, est_visible, adresse, code_postal, ville, created_at) VALUES ('b1111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', 'Salle des Fêtes', 'Grande salle polyvalente pour événements', 200, true, '1 Place de la Mairie', '75001', 'Paris', NOW());
INSERT INTO bien (id_bien, id_cat_bien, nom, description, capacite, est_visible, adresse, code_postal, ville, created_at) VALUES ('b2222222-2222-2222-2222-222222222222', 'c1111111-1111-1111-1111-111111111111', 'Salle de Réunion A', 'Salle de réunion équipée', 30, true, '1 Place de la Mairie', '75001', 'Paris', NOW());
INSERT INTO bien (id_bien, id_cat_bien, nom, description, capacite, est_visible, adresse, code_postal, ville, created_at) VALUES ('b3333333-3333-3333-3333-333333333333', 'c2222222-2222-2222-2222-222222222222', 'Gymnase Municipal', 'Gymnase avec équipements sportifs complets', 100, true, '10 Rue du Sport', '75001', 'Paris', NOW());
INSERT INTO bien (id_bien, id_cat_bien, nom, description, capacite, est_visible, adresse, code_postal, ville, created_at) VALUES ('b4444444-4444-4444-4444-444444444444', 'c2222222-2222-2222-2222-222222222222', 'Terrain de Football', 'Terrain de football en gazon synthétique', 50, true, '15 Avenue des Sports', '75001', 'Paris', NOW());
INSERT INTO bien (id_bien, id_cat_bien, nom, description, capacite, est_visible, adresse, code_postal, ville, created_at) VALUES ('b5555555-5555-5555-5555-555555555555', 'c3333333-3333-3333-3333-333333333333', 'Chaises pliantes (lot de 50)', 'Lot de 50 chaises pliantes', 50, true, '1 Place de la Mairie', '75001', 'Paris', NOW());

-- Insertion des tarifs
INSERT INTO tarif (id_tarif, id_bien, prix_base, prix_resident, prix_niveau1, prix_niveau2, prix_niveau3, created_at) VALUES ('t1111111-1111-1111-1111-111111111111', 'b1111111-1111-1111-1111-111111111111', 500.00, 400.00, 350.00, 300.00, 250.00, NOW());
INSERT INTO tarif (id_tarif, id_bien, prix_base, prix_resident, prix_niveau1, prix_niveau2, prix_niveau3, created_at) VALUES ('t2222222-2222-2222-2222-222222222222', 'b2222222-2222-2222-2222-222222222222', 100.00, 80.00, 70.00, 60.00, 50.00, NOW());
INSERT INTO tarif (id_tarif, id_bien, prix_base, prix_resident, prix_niveau1, prix_niveau2, prix_niveau3, created_at) VALUES ('t3333333-3333-3333-3333-333333333333', 'b3333333-3333-3333-3333-333333333333', 200.00, 150.00, 130.00, 110.00, 90.00, NOW());
INSERT INTO tarif (id_tarif, id_bien, prix_base, prix_resident, prix_niveau1, prix_niveau2, prix_niveau3, created_at) VALUES ('t4444444-4444-4444-4444-444444444444', 'b4444444-4444-4444-4444-444444444444', 150.00, 120.00, 100.00, 80.00, 60.00, NOW());
INSERT INTO tarif (id_tarif, id_bien, prix_base, prix_resident, prix_niveau1, prix_niveau2, prix_niveau3, created_at) VALUES ('t5555555-5555-5555-5555-555555555555', 'b5555555-5555-5555-5555-555555555555', 50.00, 40.00, 35.00, 30.00, 25.00, NOW());

-- Insertion des groupes
INSERT INTO groupe (id_groupe, nom, description, type_groupe, type_exoneration, niveau_tarif, created_at) VALUES ('g1111111-1111-1111-1111-111111111111', 'Association Sportive', 'Association sportive locale', 'ASSOCIATION', 'ASSOCIATION_SPORTIVE', 2, NOW());
INSERT INTO groupe (id_groupe, nom, description, type_groupe, type_exoneration, niveau_tarif, created_at) VALUES ('g2222222-2222-2222-2222-222222222222', 'Club des Seniors', 'Club pour les personnes âgées', 'ASSOCIATION', 'ASSOCIATION_CULTURELLE', 3, NOW());
INSERT INTO groupe (id_groupe, nom, description, type_groupe, type_exoneration, niveau_tarif, created_at) VALUES ('g3333333-3333-3333-3333-333333333333', 'Comité des Fêtes', 'Organisation des événements municipaux', 'ASSOCIATION', 'ASSOCIATION_CULTURELLE', 2, NOW());
INSERT INTO groupe (id_groupe, nom, description, type_groupe, type_exoneration, niveau_tarif, created_at) VALUES ('g4444444-4444-4444-4444-444444444444', 'Club de Lecture', 'Passionnés de littérature', 'ASSOCIATION', 'ASSOCIATION_CULTURELLE', 3, NOW());

-- Insertion des membres de groupes
INSERT INTO user_groupe (id_user, id_groupe, role_groupe, statut, joined_at) VALUES ('11111111-1111-1111-1111-111111111111', 'g1111111-1111-1111-1111-111111111111', 'MEMBRE', 'ACCEPTE', NOW());
INSERT INTO user_groupe (id_user, id_groupe, role_groupe, statut, joined_at) VALUES ('11111111-1111-1111-1111-111111111111', 'g2222222-2222-2222-2222-222222222222', 'MEMBRE', 'ACCEPTE', NOW());
INSERT INTO user_groupe (id_user, id_groupe, role_groupe, statut, joined_at) VALUES ('11111111-1111-1111-1111-111111111111', 'g3333333-3333-3333-3333-333333333333', 'ADMIN', 'ACCEPTE', NOW());
INSERT INTO user_groupe (id_user, id_groupe, role_groupe, statut, joined_at) VALUES ('22222222-2222-2222-2222-222222222222', 'g1111111-1111-1111-1111-111111111111', 'ADMIN', 'ACCEPTE', NOW());
INSERT INTO user_groupe (id_user, id_groupe, role_groupe, statut, joined_at) VALUES ('22222222-2222-2222-2222-222222222222', 'g3333333-3333-3333-3333-333333333333', 'MEMBRE', 'ACCEPTE', NOW());
INSERT INTO user_groupe (id_user, id_groupe, role_groupe, statut, joined_at) VALUES ('33333333-3333-3333-3333-333333333333', 'g2222222-2222-2222-2222-222222222222', 'MEMBRE', 'ACCEPTE', NOW());
INSERT INTO user_groupe (id_user, id_groupe, role_groupe, statut, joined_at) VALUES ('33333333-3333-3333-3333-333333333333', 'g4444444-4444-4444-4444-444444444444', 'ADMIN', 'ACCEPTE', NOW());

-- Insertion de réservations de test
INSERT INTO reservation (id_reservation, id_user, id_bien, id_groupe, date_debut, date_fin, statut, statut_caution, created_at) VALUES ('r1111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'b1111111-1111-1111-1111-111111111111', 'g1111111-1111-1111-1111-111111111111', '2026-05-15 14:00:00', '2026-05-15 18:00:00', 'CONFIRMEE', 'NON_REQUISE', NOW());
INSERT INTO reservation (id_reservation, id_user, id_bien, id_groupe, date_debut, date_fin, statut, statut_caution, created_at) VALUES ('r2222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', 'b3333333-3333-3333-3333-333333333333', NULL, '2026-05-20 09:00:00', '2026-05-20 12:00:00', 'EN_ATTENTE', 'NON_REQUISE', NOW());
