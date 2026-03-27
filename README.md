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

### Variables d'environnement / GitHub Secrets

Les secrets suivants doivent être configurés dans GitHub Secrets pour le pipeline CI :

- `JWT_SECRET` : Clé secrète pour la génération des tokens JWT
- `SMTP_USERNAME` : Nom d'utilisateur SMTP pour l'envoi d'emails
- `SMTP_PASSWORD` : Mot de passe SMTP pour l'envoi d'emails

### Base de données

Créer une base de données PostgreSQL :

```sql
CREATE DATABASE mairie_db;
```

Configurer les propriétés dans `application.properties` :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mairie_db
spring.datasource.username=postgres
spring.datasource.password=postgres
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
