# Système de Gestion d'Événements - G_EVENT

## Description du projet

G_EVENT est une application complète de gestion d'événements permettant d'organiser, suivre et gérer différents types d'événements tels que des conférences et concerts. Cette application repose sur une architecture REST avec persistance JSON et implémente plusieurs patterns de conception, notamment Observer, Singleton et DTO.

## Table des matières

1. [Fonctionnalités](#fonctionnalités)
2. [Architecture technique](#architecture-technique)
3. [Structure du projet](#structure-du-projet)
4. [Prérequis](#prérequis)
5. [Installation et démarrage](#installation-et-démarrage)
6. [Documentation API](#documentation-api)
8. [Modèle de données](#modèle-de-données)
9. [Technologies utilisées](#technologies-utilisées)
10. [Patterns de conception](#patterns-de-conception)
11. [Tests](#tests)
12. [Auteur](#auteur)

## Fonctionnalités

L'application permet de:

### Gestion des Événements:
- Créer deux types d'événements: conférences et concerts
- Consulter, modifier et supprimer des événements
- Annuler des événements avec notification automatique aux participants
- Rechercher des événements par lieu
- Lister les événements disponibles (non complets et non annulés)
- **Persistance automatique en fichiers JSON**

### Gestion des Participants:
- Inscrire des participants ordinaires et des organisateurs
- Consulter, modifier et supprimer des profils de participants
- Rechercher des participants par nom
- Assigner des organisateurs à des événements
- **Sauvegarde automatique des données**

### Gestion des Inscriptions:
- Inscrire et désinscrire des participants aux événements
- Limiter les inscriptions selon la capacité maximale de l'événement
- Notifier les participants lors de modifications d'événements

### Notifications:
- Système de notification asynchrone des participants
- Notifications lors d'annulation ou modification d'événements
- **Simulation d'envoi d'emails pour le développement**

## Architecture technique

L'application est structurée selon le modèle MVC (Modèle-Vue-Contrôleur):

- **Modèle**: Entités POJO avec sérialisation JSON
- **Contrôleur**: Logique métier implémentée dans les services
- **Persistance**: Fichiers JSON avec repositories personnalisés

L'architecture respecte les principes de séparation des responsabilités et d'injection de dépendances.

## Structure du projet

### Backend (Spring Boot)
```
/src
  /main
    /java/com/project/POO
      /config        - Configuration Spring Boot et initialisation JSON
      /controller    - Contrôleurs REST
      /dto           - Objets de transfert de données
      /exception     - Exceptions personnalisées et gestionnaire global
      /model         - Entités POJO avec annotations JSON
      /observer      - Interfaces pour le pattern Observer
      /repository    - Repositories JSON personnalisés
      /service       - Services métier
      /utils         - Classes utilitaires (JsonUtils)
    /resources
      application.properties - Configuration de l'application
  /test
    /java/com/project/POO  - Tests unitaires et d'intégration
/data                      - Dossier des fichiers JSON (auto-créé)
  evenements.json         - Données des événements
  participants.json       - Données des participants
```



## Prérequis

### Backend
- **JDK 21** ou supérieur
- **Maven 3.9.9** ou supérieur
- **Un IDE Java** (IntelliJ IDEA, Eclipse, VS Code)



> **Note**: Aucune base de données externe n'est requise - l'application utilise des fichiers JSON pour la persistance.

## Installation et démarrage


```bash
# Cloner le dépôt
git clone https://github.com/PIO-VIA/API_gestion_devenement.git
cd g-event

# Compiler et lancer l'application
./mvnw clean spring-boot:run

# Sous Windows
mvnw.cmd clean spring-boot:run
```

L'API sera disponible à l'adresse: `http://localhost:8080`

Les fichiers JSON seront automatiquement créés dans le dossier `data/` au premier démarrage.


## Documentation API

### Endpoints principaux:

#### Événements
- `GET /api/evenements` - Liste tous les événements
- `GET /api/evenements/{id}` - Récupère un événement par ID
- `POST /api/evenements/conferences` - Crée une nouvelle conférence
- `POST /api/evenements/concerts` - Crée un nouveau concert
- `PUT /api/evenements/{id}` - Met à jour un événement
- `DELETE /api/evenements/{id}` - Supprime un événement
- `PUT /api/evenements/{id}/annuler` - Annule un événement
- `GET /api/evenements/recherche?lieu={lieu}` - Recherche d'événements par lieu
- `GET /api/evenements/disponibles` - Liste les événements disponibles

#### Participants
- `GET /api/participants` - Liste tous les participants
- `GET /api/participants/{id}` - Récupère un participant par ID
- `POST /api/participants` - Crée un nouveau participant
- `POST /api/participants/organisateurs` - Crée un nouvel organisateur
- `PUT /api/participants/{id}` - Met à jour un participant
- `DELETE /api/participants/{id}` - Supprime un participant
- `GET /api/participants/recherche?nom={nom}` - Recherche de participants par nom

#### Inscriptions
- `POST /api/evenements/{evenementId}/participants/{participantId}` - Inscrit un participant à un événement
- `DELETE /api/evenements/{evenementId}/participants/{participantId}` - Désinscrit un participant d'un événement

### Test des endpoints

Vous pouvez tester l'API avec:
- **Postman** ou **Insomnia**
- **curl** en ligne de commande
- Swagger

Exemple avec curl:
```bash
# Créer une conférence
curl -X POST http://localhost:8080/api/evenements/conferences \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Conférence IA 2024",
    "date": "2024-12-15T14:00:00",
    "lieu": "Salle de Conférence",
    "capaciteMax": 100,
    "theme": "Intelligence Artificielle"
  }'
```


## Modèle de données

### Persistance JSON
Les données sont stockées dans des fichiers JSON dans le dossier `data/`:

```json
//evenements.json
[
  {
    "@type": "com.project.POO.model.Conference",
    "id": "uuid-123",
    "nom": "Conférence IA",
    "date": "2024-12-15T14:00:00",
    "lieu": "Salle A",
    "capaciteMax": 100,
    "annule": false,
    "theme": "Intelligence Artificielle",
    "participants": [],
    "intervenants": []
  }
]
```

### Classes principales

#### Classe Evenement (abstraite)
- **id**: Identifiant unique (UUID)
- **nom**: Nom de l'événement
- **date**: Date et heure de l'événement
- **lieu**: Lieu de l'événement
- **capaciteMax**: Capacité maximale
- **annule**: État d'annulation
- **participants**: Liste des participants inscrits
- **organisateur**: Organisateur de l'événement

#### Classes dérivées
- **Conference**: Inclut thème et intervenants
- **Concert**: Inclut artiste et genre musical

#### Classe Participant
- **id**: Identifiant unique (UUID)
- **nom**: Nom du participant
- **email**: Email du participant
- **evenementsInscrits**: Liste des événements auxquels le participant est inscrit

#### Classe Organisateur (dérive de Participant)
- **evenementsOrganises**: Liste des événements organisés

## Technologies utilisées

### Backend
- **Spring Boot 3.4.5**: Framework principal
- **Spring MVC**: Contrôleurs REST
- **Jackson**: Sérialisation/Désérialisation JSON
- **Lombok**: Réduction du code boilerplate
- **JUnit 5**: Tests unitaires et d'intégration
- **Mockito**: Mocking pour les tests


### Persistance
- **Fichiers JSON**: Stockage des données
- **Jackson ObjectMapper**: Sérialisation avec support d'héritage
- **Repositories personnalisés**: Abstraction de la persistance


## Patterns de conception

### Pattern Observer
Implémenté pour les notifications d'événements. Quand un événement est modifié ou annulé, tous les participants inscrits sont automatiquement notifiés.

```java
// Interfaces
public interface EvenementObservable {
    void subscribe(ParticipantObserver observer);
    void unsubscribe(ParticipantObserver observer);
    void notifyObservers(String message);
}

public interface ParticipantObserver {
    void update(String message);
}
```

### Pattern Singleton
Implémenté pour la gestion centralisée des événements.

```java
@Service
public class GestionEvenements {
    private final Map<String, Evenement> evenements = new HashMap<>();
    
    public void ajouterEvenement(Evenement evenement) {
        evenements.put(evenement.getId(), evenement);
    }
    // ...
}
```

### Pattern DTO (Data Transfer Object)
Utilisé pour séparer la représentation API des entités internes.

```java
// DTOs
public class EvenementDto { /* ... */ }
public class ParticipantDto { /* ... */ }

// Conversions
private EvenementDto convertToDto(Evenement evenement) { /* ... */ }
private Evenement convertToEntity(EvenementDto dto) { /* ... */ }
```

### Pattern Repository
Implémentation personnalisée pour la persistance JSON.

```java
@Repository
public class JsonEvenementRepository {
    public List<Evenement> findAll() { /* ... */ }
    public Evenement save(Evenement evenement) { /* ... */ }
    // ...
}
```

## Tests

L'application est couverte par une suite complète de tests unitaires et d'intégration.

- Tests unitaires pour les services, repositories et utilitaires
- Tests d'intégration pour les contrôleurs REST
- Tests pour les patterns de conception implémentés
- **Tests de sérialisation JSON**

Pour exécuter les tests:

```bash
./mvnw test
```

Pour générer un rapport de couverture de code:

```bash
./mvnw jacoco:report
```

Le rapport sera généré dans `target/site/jacoco/index.html`.


## Auteur

Ce projet a été développé par **SOUNTSA DJIELE PIO VIANNEY** dans le cadre d'un apprentissage avancé de Spring Boot avec sérialisation JSON .

---
