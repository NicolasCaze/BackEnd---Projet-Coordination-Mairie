-- Ajouter la colonne role_groupe dans user_groupe
ALTER TABLE user_groupe ADD COLUMN IF NOT EXISTS role_groupe VARCHAR(255) DEFAULT 'MEMBRE';

-- Ajouter une contrainte de vérification pour role_groupe
ALTER TABLE user_groupe DROP CONSTRAINT IF EXISTS user_groupe_role_groupe_check;
ALTER TABLE user_groupe ADD CONSTRAINT user_groupe_role_groupe_check 
CHECK (role_groupe IN ('ADMIN', 'MEMBRE'));

-- Modifier la contrainte users_role_check pour inclure SUPER_ADMIN
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users ADD CONSTRAINT users_role_check 
CHECK (role IN ('SUPER_ADMIN', 'ADMIN', 'USER', 'MODERATEUR', 'TUTORED', 'SECRETARY'));

-- Mettre à jour l'utilisateur admin pour qu'il soit SUPER_ADMIN
UPDATE users SET role = 'SUPER_ADMIN' WHERE email = 'admin@mairie.fr';
