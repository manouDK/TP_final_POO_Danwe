package com.project.POO;

import com.project.POO.model.Conference;
import com.project.POO.model.Concert;
import com.project.POO.model.Evenement;
import com.project.POO.repository.JsonEvenementRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsonEvenementRepositoryTest {

    private JsonEvenementRepository repository;
    private Conference conference;
    private Concert concert;

    @TempDir
    static Path tempDir;

    @BeforeAll
    void setUpClass() {
        // Configurer le répertoire temporaire pour les tests
        System.setProperty("user.dir", tempDir.toString());
    }

    @BeforeEach
    void setUp() throws IOException {
        // Nettoyer les fichiers existants
        Path dataDir = tempDir.resolve("data");
        if (Files.exists(dataDir)) {
            Files.walk(dataDir)
                    .sorted((a, b) -> b.compareTo(a)) // Supprimer les fichiers avant les dossiers
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // Ignorer les erreurs de suppression
                        }
                    });
        }

        repository = new JsonEvenementRepository();

        // Créer des événements de test
        conference = new Conference("TechConf 2024",
                LocalDateTime.now().plusDays(30),
                "Salle de Conférence",
                100,
                "Intelligence Artificielle");

        concert = new Concert("Live Music",
                LocalDateTime.now().plusDays(15),
                "Stadium",
                5000,
                "Artiste Populaire",
                "Pop");
    }

    @Test
    @DisplayName("Sauvegarder un événement crée un ID et le stocke")
    void save_CreatesIdAndStoresEvent() {
        // Act
        Evenement savedEvent = repository.save(conference);

        // Assert
        assertNotNull(savedEvent.getId());
        assertEquals(conference.getNom(), savedEvent.getNom());

        // Vérifier que l'événement est dans le repository
        Optional<Evenement> retrieved = repository.findById(savedEvent.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(savedEvent.getId(), retrieved.get().getId());
    }

    @Test
    @DisplayName("Récupérer tous les événements retourne la liste complète")
    void findAll_ReturnsAllEvents() {
        // Arrange
        repository.save(conference);
        repository.save(concert);

        // Act
        List<Evenement> events = repository.findAll();

        // Assert
        assertEquals(2, events.size());
        assertTrue(events.stream().anyMatch(e -> e.getNom().equals(conference.getNom())));
        assertTrue(events.stream().anyMatch(e -> e.getNom().equals(concert.getNom())));
    }

    @Test
    @DisplayName("Rechercher par ID retourne l'événement correct")
    void findById_ReturnsCorrectEvent() {
        // Arrange
        Evenement saved = repository.save(conference);

        // Act
        Optional<Evenement> found = repository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(conference.getNom(), found.get().getNom());
    }

    @Test
    @DisplayName("Rechercher par ID inexistant retourne Optional vide")
    void findById_ReturnsEmptyForNonExistentId() {
        // Act
        Optional<Evenement> found = repository.findById("non-existent-id");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Supprimer un événement l'enlève du stockage")
    void delete_RemovesEventFromStorage() {
        // Arrange
        Evenement saved = repository.save(conference);

        // Act
        repository.delete(saved);

        // Assert
        Optional<Evenement> found = repository.findById(saved.getId());
        assertFalse(found.isPresent());
        assertEquals(0, repository.count());
    }

    @Test
    @DisplayName("Vérifier l'existence par nom et date")
    void existsByNomAndDate_ChecksCorrectly() {
        // Arrange
        repository.save(conference);

        // Act & Assert
        assertTrue(repository.existsByNomAndDate(conference.getNom(), conference.getDate()));
        assertFalse(repository.existsByNomAndDate("Autre Nom", conference.getDate()));
        assertFalse(repository.existsByNomAndDate(conference.getNom(), LocalDateTime.now()));
    }

    @Test
    @DisplayName("Rechercher par lieu fonctionne avec recherche partielle")
    void findByLieuContainingIgnoreCase_WorksWithPartialMatch() {
        // Arrange
        repository.save(conference);
        repository.save(concert);

        // Act
        List<Evenement> salleEvents = repository.findByLieuContainingIgnoreCase("salle");
        List<Evenement> stadiumEvents = repository.findByLieuContainingIgnoreCase("STADIUM");

        // Assert
        assertEquals(1, salleEvents.size());
        assertEquals(conference.getNom(), salleEvents.get(0).getNom());

        assertEquals(1, stadiumEvents.size());
        assertEquals(concert.getNom(), stadiumEvents.get(0).getNom());
    }

    @Test
    @DisplayName("La persistance fonctionne entre les instances")
    void persistence_WorksBetweenInstances() {
        // Arrange - Sauvegarder avec la première instance
        Evenement saved = repository.save(conference);
        String savedId = saved.getId();

        // Act - Créer une nouvelle instance du repository
        JsonEvenementRepository newRepository = new JsonEvenementRepository();

        // Assert - Les données doivent être chargées
        Optional<Evenement> loaded = newRepository.findById(savedId);
        assertTrue(loaded.isPresent());
        assertEquals(conference.getNom(), loaded.get().getNom());
        assertEquals(1, newRepository.count());
    }

    @Test
    @DisplayName("Les types d'événements sont préservés lors de la sérialisation")
    void eventTypes_ArePreservedDuringSerialization() {
        // Arrange
        Evenement savedConference = repository.save(conference);
        Evenement savedConcert = repository.save(concert);

        // Act - Recharger depuis le stockage
        JsonEvenementRepository newRepository = new JsonEvenementRepository();
        Optional<Evenement> loadedConference = newRepository.findById(savedConference.getId());
        Optional<Evenement> loadedConcert = newRepository.findById(savedConcert.getId());

        // Assert
        assertTrue(loadedConference.isPresent());
        assertTrue(loadedConference.get() instanceof Conference);

        assertTrue(loadedConcert.isPresent());
        assertTrue(loadedConcert.get() instanceof Concert);

        // Vérifier les propriétés spécifiques
        Conference conf = (Conference) loadedConference.get();
        assertEquals("Intelligence Artificielle", conf.getTheme());

        Concert conc = (Concert) loadedConcert.get();
        assertEquals("Artiste Populaire", conc.getArtiste());
        assertEquals("Pop", conc.getGenreMusical());
    }
}