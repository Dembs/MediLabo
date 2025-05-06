# MediLabo Solutions

## Description

MediLabo Solutions est une application web de gestion de dossiers patients pour un cabinet médical. Elle permet de suivre les informations des patients, leurs notes médicales, et d'évaluer leur risque de diabète. L'application est architecturée en microservices et conteneurisée avec Docker.

## Fonctionnalités principales

*   Gestion des patients : ajout, consultation, modification, suppression.
*   Gestion des notes médicales par patient.
*   Évaluation du risque de diabète basée sur l'âge, le genre et les notes du patient.
*   Interface utilisateur web pour accéder aux fonctionnalités.
*   API Gateway pour exposer les services backend.

## Technologies utilisées

*   **Langage :** Java 17
*   **Frameworks :** Spring Boot 3.x, Spring Cloud Gateway, Spring Security, Spring Data JPA, Spring Web
*   **Bases de données :**
    *   MySQL (pour le microservice des patients)
    *   MongoDB (pour le microservice des notes)
*   **Frontend :** Thymeleaf, HTML, CSS (Bootstrap 5)
*   **Conteneurisation :** Docker, Docker Compose
*   **Tests :** JUnit 5, Mockito
*   **Build :** Maven

## Prérequis

*   JDK 17 ou supérieur
*   Maven 3.6 ou supérieur
*   Docker et Docker Compose
*   Git

## Démarrage Rapide

### 1. Cloner le repository

```bash
git clone https://github.com/Dembs/MediLabo
cd MediLabo
```

### 2. Configuration

Les configurations des services se trouvent dans les fichiers `application.properties` de chaque microservice (ex: `patient-service/src/main/resources/application.properties`).
Les variables d'environnement pour Docker sont définies dans le fichier `docker-compose.yml`.

**Variables d'environnement importantes (pour Docker) :**

*   `MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD` pour la base de données MySQL.
*   `MONGO_INITDB_DATABASE` pour la base de données MongoDB.
*   `SPRING_SECURITY_USER_NAME`, `SPRING_SECURITY_USER_PASSWORD` pour l'API Gateway.
*   `BACKEND_API_USERNAME`, `BACKEND_API_PASSWORD` pour le microservice frontend (pour l'authentification Basic vers l'API Gateway).

### 3. Compilation des modules Maven

Naviguez dans le répertoire de chaque microservice et compilez avec Maven :

```bash
# Pour chaque microservice (patient-service, notes-service, diabetes-service, api-gateway, front-end)
cd <nom-du-microservice>
mvn clean package -DskipTests
cd ..
```

### 4. Lancement avec Docker Compose

Depuis la racine du projet :

```bash
docker-compose up --build
```

L'application devrait être accessible via les URLs suivantes :

*   **Frontend :** `http://localhost:8082/ui/patients`
*   **API Gateway :** `http://localhost:8080/patients`

Les identifiants par défaut pour se connecter à l'interface utilisateur sont définis par `BACKEND_API_USERNAME` et `BACKEND_API_PASSWORD` (ex: user / password).
Les identifiants par défaut pour l'API Gateway (si testée directement) sont définis par `SPRING_SECURITY_USER_NAME` et `SPRING_SECURITY_USER_PASSWORD`.

## Utilisation

1.  Accédez à `http://localhost:8082/ui/patients` dans votre navigateur.
2.  Connectez-vous avec les identifiants configurés.
3.  Vous arriverez sur la liste des patients.
4.  Vous pouvez ajouter, voir les détails, modifier ou supprimer un patient.
5.  Depuis la page de détails d'un patient, vous pouvez voir ses notes, en ajouter, et voir son évaluation du risque de diabète.

## Structure du Projet (Microservices)

*   **`api-gateway`**: Point d'entrée unique pour toutes les requêtes frontend. Gère le routage et la sécurité.
*   **`patient-service`**: Gère les informations des patients (CRUD). Utilise MySQL.
*   **`notes-service`**: Gère les notes médicales associées aux patients. Utilise MongoDB.
*   **`diabetes-service`**: Évalue le risque de diabète en se basant sur les informations du patient et ses notes.
*   **`front-end`**: Interface utilisateur web construite avec Spring Boot et Thymeleaf.

## Green Code : Éco-conception Logicielle

### Quel est l'objectif du Green Code ?

L'objectif principal du Green Code est de concevoir et développer des applications 
numériques qui minimisent leur impact environnemental. 
Cela implique de réduire la consommation d'énergie et d'optimiser l'utilisation 
des ressources (CPU, mémoire, réseau).

### Bonnes pratiques générales du Green Code

*   **Optimiser les algorithmes et les structures de données :** Choisir des solutions performantes pour réduire la charge CPU.
*   **Limiter les transferts de données :** Ne transférer que les informations strictement nécessaires, que ce soit entre services ou vers le client.
*   **Gestion efficiente de la mémoire :** Éviter les fuites de mémoire et optimiser l'allocation des objets.
*   **Utiliser des technologies et des formats de données légers.**
*   **Configurer judicieusement les logs** pour éviter une surcharge inutile.
*   **Optimiser les images et les ressources statiques**.
*   **Utiliser des images Docker de base minimales** et des builds multi-étapes.

### Points d'amélioration "Green" pour le projet MediLabo

1.  **Optimisation des requêtes :**
    Mettre en place une pagination pour l'affichage des listes de patients et de notes afin de ne pas charger toutes les données en une fois.
2.  **Mise en cache :**
    Envisager un cache pour les données fréquemment consultées et peu volatiles (ex: détails d'un patient, évaluation de risque) afin de diminuer les accès aux bases de données et les recalculs.
3. **Optimisation Frontend :**
    *   **Optimisation des images :** S'assurer que le logo et autres images éventuelles sont compressés et au format adéquat (ex: WebP).
    *   **Lazy Loading :** Si des sections de page ou des images ne sont pas immédiatement visibles, envisager le lazy loading.
4. **Optimisation des logs dans les services :**
   Configurer les niveaux de log de manière appropriée pour la production pour réduire les I/O disque et la charge CPU.

