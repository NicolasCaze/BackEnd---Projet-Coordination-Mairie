# BackEnd - Projet Coordination Mairie

[![CI Pipeline](https://github.com/NicolasCaze/BackEnd---Projet-Coordination-Mairie/actions/workflows/ci.yml/badge.svg)](https://github.com/NicolasCaze/BackEnd---Projet-Coordination-Mairie/actions/workflows/ci.yml)

## Description

API REST pour la gestion des réservations de biens municipaux, des utilisateurs, des groupes et des pièces justificatives. Système de coordination pour la mairie.

## Technologies

- Java 17
- Spring Boot 3.x
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL
- Springdoc OpenAPI
- Maven

## Prérequis

- JDK 17+
- Maven 3.6+
- PostgreSQL 15+

## Configuration

### Variables d'environnement

**⚠️ IMPORTANT : Aucun secret ne doit être commité en clair dans le repository.**

Toutes les variables sensibles sont externalisées via des variables d'environnement. Un fichier `.env.example` est fourni à la racine du projet avec toutes les variables nécessaires.

#### Configuration locale

1. Copier le fichier `.env.example` vers `.env` :
```bash
cp .env.example .env
```

2. Éditer le fichier `.env` avec vos valeurs :
```bash
nano .env
```

3. Le fichier `.env` est automatiquement ignoré par Git (voir `.gitignore`)

#### Variables requises

##### JWT (Obligatoires)
- `JWT_SECRET` : Clé secrète pour la génération et validation des tokens JWT (min 256 bits)
- `JWT_EXPIRATION` : Durée de validité du token en secondes (défaut: 86400 = 24h)
- `JWT_REFRESH_EXPIRATION` : Durée de validité du refresh token en secondes (défaut: 604800 = 7 jours)

##### Base de données PostgreSQL
- `DATABASE_URL` : URL de connexion JDBC (défaut: jdbc:postgresql://localhost:5432/mairie_db)
- `DATABASE_USERNAME` : Nom d'utilisateur PostgreSQL (défaut: postgres)
- `DATABASE_PASSWORD` : Mot de passe PostgreSQL (défaut: postgres)
- `JPA_DDL_AUTO` : Stratégie de gestion du schéma (défaut: update)
- `JPA_SHOW_SQL` : Afficher les requêtes SQL dans les logs (défaut: false)

##### Upload de fichiers
- `UPLOAD_DIRECTORY` : Répertoire de stockage des fichiers (défaut: temp-uploads)
- `UPLOAD_MAX_SIZE` : Taille maximale en octets (défaut: 10485760 = 10MB)

##### SMTP (Obligatoires pour l'envoi d'emails)
- `SMTP_HOST` : Serveur SMTP (défaut: smtp.gmail.com)
- `SMTP_PORT` : Port SMTP (défaut: 587)
- `SMTP_USERNAME` : Nom d'utilisateur SMTP (généralement l'adresse email)
- `SMTP_PASSWORD` : Mot de passe SMTP ou mot de passe d'application
- `SMTP_AUTH` : Activer l'authentification SMTP (défaut: true)
- `SMTP_STARTTLS` : Activer STARTTLS (défaut: true)
- `MAIL_SECRETARIAT` : Adresse email du secrétariat (défaut: secretariat@mairie.fr)

#### GitHub Secrets (CI/CD)

Pour le pipeline CI, configurer les secrets suivants dans GitHub Actions :
- `JWT_SECRET`
- `SMTP_USERNAME`
- `SMTP_PASSWORD`

### Base de données

Créer une base de données PostgreSQL :

```sql
CREATE DATABASE mairie_db;
```

## Installation et démarrage

```bash
# Cloner le repository
git clone https://github.com/NicolasCaze/BackEnd---Projet-Coordination-Mairie.git
cd BackEnd---Projet-Coordination-Mairie

# Compiler le projet
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

## Tests

```bash
# Exécuter les tests
mvn test

# Exécuter les tests avec vérification
mvn verify
```

## Documentation API

Une fois l'application démarrée, la documentation Swagger UI est accessible à :

- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **API Docs JSON** : http://localhost:8080/v3/api-docs
- **API Docs YAML** : http://localhost:8080/v3/api-docs.yaml

Le fichier `openapi.yaml` versionné est disponible à la racine du projet.

## Pipeline CI/CD

Le projet utilise GitHub Actions pour l'intégration continue :

- **Déclenchement** : Sur chaque Pull Request vers `main` et push sur `main`
- **Étapes** :
  1. Checkout du code
  2. Configuration JDK 17
  3. Démarrage PostgreSQL (service)
  4. Exécution des tests (`mvn test`)
  5. Vérification du build (`mvn verify`)
  6. Upload des rapports de tests et de couverture

**Un build rouge bloque le merge de la PR.**

## Structure du projet

```
src/
├── main/
│   ├── java/com/app/
│   │   ├── annotation/      # Annotations personnalisées (@Audited)
│   │   ├── aspect/          # Aspects AOP (audit logging)
│   │   ├── config/          # Configuration (Security, OpenAPI, etc.)
│   │   ├── controller/      # Contrôleurs REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entités JPA
│   │   ├── exception/       # Exceptions personnalisées
│   │   ├── repository/      # Repositories JPA
│   │   ├── service/         # Services métier
│   │   └── util/            # Utilitaires (JWT, etc.)
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/app/        # Tests unitaires et d'intégration
```

## Endpoints principaux

- `/auth/login` - Authentification
- `/auth/register` - Inscription
- `/users` - Gestion des utilisateurs
- `/biens` - Gestion des biens municipaux
- `/groupes` - Gestion des groupes
- `/reservations` - Gestion des réservations
- `/admin` - Administration (délégations, impersonation, audit logs)
- `/api/upload` - Upload de pièces justificatives
