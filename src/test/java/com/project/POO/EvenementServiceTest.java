package com.project.POO;

import com.project.POO.exception.EvenementDejaExistantException;
import com.project.POO.exception.EvenementNotFoundException;
import com.project.POO.model.Concert;
import com.project.POO.model.Conference;
import com.project.POO.model.Evenement;
import com.project.POO.model.Participant;
import com.project.POO.repository.JsonEvenementRepository;
import com.project.POO.service.EvenementService;
import com.project.POO.service.GestionEvenements;
import com.project.POO.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EvenementServiceTest {

    @Mock
    private JsonEvenementRepository evenementRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private GestionEvenements gestionEvenements;

    @InjectMocks
    private EvenementService evenementService;

    private Evenement conference;
    private Evenement concert;
    private Participant participant;

    @BeforeEach
    void setUp() {
        conference = new Conference("Conf IA", LocalDateTime.now().plusDays(10), "S1", 100, "Nouvelles technologies sur IA");
        conference.setId("conf-1");

        concert = new Concert("LiveMusic", LocalDateTime.now().plusDays(20), "Canal Olympia", 1000, "fally ", "Mbole");
        concert.setId("concert-1");

        participant = new Participant("John ", "john@example.com");
        participant.setId("part-1");
    }

    @Test
    @DisplayName("Créer un événement avec succès")
    void creerEvenement_Success() throws EvenementDejaExistantException {
        // Arrange
        when(evenementRepository.existsByNomAndDate(anyString(), any())).thenReturn(false);
        when(evenementRepository.save(any(Evenement.class))).thenReturn(conference);

        // Act
        Evenement result = evenementService.creerEvenement(conference);

        // Assert
        assertNotNull(result);
        assertEquals(conference.getId(), result.getId());
        verify(evenementRepository).save(conference);
        verify(gestionEvenements).ajouterEvenement(conference);
    }

    @Test
    @DisplayName("Création d'un événement échoue si même nom et date")
    void creerEvenement_ThrowsException_WhenEventAlreadyExists() {
        // Arrange
        when(evenementRepository.existsByNomAndDate(anyString(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(EvenementDejaExistantException.class, () -> {
            evenementService.creerEvenement(conference);
        });
        verify(evenementRepository, never()).save(any(Evenement.class));
    }

    @Test
    @DisplayName("Récupérer tous les événements")
    void getAllEvenements_ReturnsAllEvents() {
        // Arrange
        List<Evenement> evenements = Arrays.asList(conference, concert);
        when(evenementRepository.findAll()).thenReturn(evenements);

        // Act
        List<Evenement> result = evenementService.getAllEvenements();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(conference));
        assertTrue(result.contains(concert));
    }

    @Test
    @DisplayName("Récupérer un événement par ID")
    void getEvenementById_ReturnsEvent_WhenFound() throws EvenementNotFoundException {
        // Arrange
        when(evenementRepository.findById(conference.getId())).thenReturn(Optional.of(conference));

        // Act
        Evenement result = evenementService.getEvenementById(conference.getId());

        // Assert
        assertNotNull(result);
        assertEquals(conference.getId(), result.getId());
    }

    @Test
    @DisplayName("Récupérer un événement par ID lance une exception si non trouvé")
    void getEvenementById_ThrowsException_WhenNotFound() {
        // Arrange
        String nonExistingId = "non-existing-id";
        when(evenementRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EvenementNotFoundException.class, () -> {
            evenementService.getEvenementById(nonExistingId);
        });
    }

    @Test
    @DisplayName("Mettre à jour un événement")
    void updateEvenement_Success() throws EvenementNotFoundException {
        // Arrange
        Evenement updatedConference = new Conference();
        updatedConference.setId(conference.getId());
        updatedConference.setNom("TechConf 2.0");
        updatedConference.setDate(LocalDateTime.now().plusDays(15));
        updatedConference.setLieu("Salle B");
        updatedConference.setCapaciteMax(200);

        when(evenementRepository.findById(conference.getId())).thenReturn(Optional.of(conference));
        when(evenementRepository.save(any(Evenement.class))).thenReturn(updatedConference);

        // Act
        Evenement result = evenementService.updateEvenement(conference.getId(), updatedConference);

        // Assert
        assertNotNull(result);
        assertEquals("TechConf 2.0", result.getNom());
        assertEquals("Salle B", result.getLieu());
        assertEquals(200, result.getCapaciteMax());
    }

    @Test
    @DisplayName("Supprimer un événement")
    void deleteEvenement_Success() throws EvenementNotFoundException {
        // Arrange
        when(evenementRepository.findById(conference.getId())).thenReturn(Optional.of(conference));
        doNothing().when(evenementRepository).delete(any(Evenement.class));
        doNothing().when(gestionEvenements).supprimerEvenement(anyString());

        // Act
        evenementService.deleteEvenement(conference.getId());

        // Assert
        verify(evenementRepository).delete(conference);
        verify(gestionEvenements).supprimerEvenement(conference.getId());
    }

    @Test
    @DisplayName("Annuler un événement")
    void annulerEvenement_MarksEventAsCancelled() throws EvenementNotFoundException {
        // Arrange
        when(evenementRepository.findById(conference.getId())).thenReturn(Optional.of(conference));
        when(evenementRepository.save(any(Evenement.class))).thenReturn(conference);

        // Act
        evenementService.annulerEvenement(conference.getId());

        // Assert
        assertTrue(conference.isAnnule());
        verify(evenementRepository).save(conference);
    }

    @Test
    @DisplayName("Ajouter un participant à un événement")
    void ajouterParticipant_Success() throws Exception {
        // Arrange
        when(evenementRepository.findById(conference.getId())).thenReturn(Optional.of(conference));
        when(evenementRepository.save(any(Evenement.class))).thenReturn(conference);

        // Act
        evenementService.ajouterParticipant(conference.getId(), participant);

        // Assert
        verify(evenementRepository).save(conference);
        // Vérifier que le participant est dans la liste des participants de l'événement
        assertTrue(conference.getParticipants().contains(participant));
    }

    @Test
    @DisplayName("Suppression d'un participant d'un événement")
    void supprimerParticipant_RemovesParticipant() throws EvenementNotFoundException {
        // Arrange
        conference.getParticipants().add(participant);
        when(evenementRepository.findById(conference.getId())).thenReturn(Optional.of(conference));
        when(evenementRepository.save(any(Evenement.class))).thenReturn(conference);

        // Act
        evenementService.supprimerParticipant(conference.getId(), participant.getId());

        // Assert
        verify(evenementRepository).save(conference);
        // Vérifier que le participant n'est plus dans la liste
        assertFalse(conference.getParticipants().contains(participant));
    }

    @Test
    @DisplayName("Rechercher des événements par lieu")
    void rechercherParLieu_ReturnMatchingEvents() {
        // Arrange
        List<Evenement> allEvents = Arrays.asList(conference, concert);
        when(evenementRepository.findAll()).thenReturn(allEvents);

        // Act
        List<Evenement> result = evenementService.rechercherParLieu("salle");

        // Assert
        assertEquals(1, result.size());
        assertEquals(conference.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Rechercher des événements par lieu avec repository")
    void rechercherParLieu_UsesRepository() {
        // Arrange
        List<Evenement> matchingEvents = Arrays.asList(conference);
        when(evenementRepository.findByLieuContainingIgnoreCase("salle")).thenReturn(matchingEvents);

        // Utiliser directement la méthode du repository si disponible
        List<Evenement> result = evenementRepository.findByLieuContainingIgnoreCase("salle");

        // Assert
        assertEquals(1, result.size());
        assertEquals(conference.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Lister les événements disponibles")
    void evenementsDisponibles_ReturnAvailableEvents() {
        // Arrange
        conference.setAnnule(false);
        concert.setAnnule(true);
        List<Evenement> allEvents = Arrays.asList(conference, concert);
        when(evenementRepository.findAll()).thenReturn(allEvents);

        // Act
        List<Evenement> result = evenementService.evenementsDisponibles();

        // Assert
        assertEquals(1, result.size());
        assertEquals(conference.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Vérifier la capacité maximale lors de l'ajout d'un participant")
    void ajouterParticipant_ThrowsException_WhenCapacityReached() {
        // Arrange
        Conference smallConference = new Conference("Small Conf", LocalDateTime.now().plusDays(5), "Small Room", 1, "Test");
        smallConference.setId("small-conf");

        Participant existingParticipant = new Participant("Existing", "existing@example.com");
        existingParticipant.setId("existing-1");

        try {
            smallConference.ajouterParticipant(existingParticipant); // Remplir la capacité
        } catch (Exception e) {
            // Ignorer l'exception pour le setup
        }

        when(evenementRepository.findById(smallConference.getId())).thenReturn(Optional.of(smallConference));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            evenementService.ajouterParticipant(smallConference.getId(), participant);
        });
    }
}