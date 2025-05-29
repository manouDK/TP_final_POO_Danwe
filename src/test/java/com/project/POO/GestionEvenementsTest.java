package com.project.POO;

import com.project.POO.model.Concert;
import com.project.POO.model.Conference;
import com.project.POO.model.Evenement;
import com.project.POO.service.GestionEvenements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GestionEvenementsTest {

    private GestionEvenements gestionEvenements;
    private Evenement conference;
    private Evenement concert;

    @BeforeEach
    void setUp() {
        // Créer une nouvelle instance pour chaque test
        gestionEvenements = new GestionEvenements();

        conference = new Conference("Conf IA", LocalDateTime.now().plusDays(10), "S1", 100, "Nouvelles technologies sur IA");
        conference.setId("conf-1");

        concert = new Concert("LiveMusic", LocalDateTime.now().plusDays(20), "Canal Olympia", 1000, "fally ", "Mbole");
        concert.setId("concert-1");
    }

    @Test
    @DisplayName("Ajouter un événement")
    void ajouterEvenement_AddsEventToMap() {
        // Act
        gestionEvenements.ajouterEvenement(conference);

        // Assert
        assertTrue(gestionEvenements.getEvenements().containsKey(conference.getId()));
        assertEquals(conference, gestionEvenements.getEvenements().get(conference.getId()));
    }

    @Test
    @DisplayName("Supprimer un événement")
    void supprimerEvenement_RemovesEventFromMap() {
        // Arrange
        gestionEvenements.ajouterEvenement(conference);

        // Act
        boolean result = gestionEvenements.supprimerEvenement(conference.getId());

        // Assert
        assertTrue(result);
        assertFalse(gestionEvenements.getEvenements().containsKey(conference.getId()));
    }

    @Test
    @DisplayName("Supprimer un événement inexistant retourne false")
    void supprimerEvenement_ReturnsFalse_WhenEventDoesNotExist() {
        // Act
        boolean result = gestionEvenements.supprimerEvenement("nonexistent-id");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Rechercher un événement existant")
    void rechercherEvenement_ReturnsEvent_WhenFound() {
        // Arrange
        gestionEvenements.ajouterEvenement(conference);

        // Act
        Optional<Evenement> result = gestionEvenements.rechercherEvenement(conference.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(conference, result.get());
    }

    @Test
    @DisplayName("Rechercher un événement inexistant retourne un Optional vide")
    void rechercherEvenement_ReturnsEmptyOptional_WhenNotFound() {
        // Act
        Optional<Evenement> result = gestionEvenements.rechercherEvenement("nonexistent-id");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("GetEvenements retourne une copie non modifiable de la map")
    void getEvenements_ReturnsUnmodifiableMap() {
        // Arrange
        gestionEvenements.ajouterEvenement(conference);
        gestionEvenements.ajouterEvenement(concert);

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            gestionEvenements.getEvenements().put("new-id", conference);
        });
    }
}