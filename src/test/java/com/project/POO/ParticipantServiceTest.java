package com.project.POO;

import com.project.POO.exception.ParticipantNotFoundException;
import com.project.POO.model.Organisateur;
import com.project.POO.model.Participant;
import com.project.POO.repository.JsonParticipantRepository;
import com.project.POO.service.NotificationService;
import com.project.POO.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {

    @Mock
    private JsonParticipantRepository participantRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ParticipantService participantService;

    private Participant participant;
    private Organisateur organisateur;

    @BeforeEach
    void setUp() {
        // Créer des objets de test
        participant = new Participant("Alice ", "alice@example.com");
        participant.setId("part-123");

        organisateur = new Organisateur("Bob ", "bob@example.com");
        organisateur.setId("org-456");
    }

    @Test
    @DisplayName("Créer un participant avec succès")
    void creerParticipant_Success() {
        // Arrange
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        // Act
        Participant result = participantService.creerParticipant(participant);

        // Assert
        assertNotNull(result);
        assertEquals(participant.getId(), result.getId());
        assertEquals(participant.getNom(), result.getNom());
        verify(participantRepository).save(participant);
    }

    @Test
    @DisplayName("Récupérer tous les participants")
    void getAllParticipants_ReturnsAllParticipants() {
        // Arrange
        List<Participant> participants = Arrays.asList(participant, organisateur);
        when(participantRepository.findAll()).thenReturn(participants);

        // Act
        List<Participant> result = participantService.getAllParticipants();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(participant));
        assertTrue(result.contains(organisateur));
    }

    @Test
    @DisplayName("Récupérer un participant par ID")
    void getParticipantById_ReturnsParticipant_WhenFound() throws ParticipantNotFoundException {
        // Arrange
        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        // Act
        Participant result = participantService.getParticipantById(participant.getId());

        // Assert
        assertNotNull(result);
        assertEquals(participant.getId(), result.getId());
    }

    @Test
    @DisplayName("Récupérer un participant par ID lance une exception si non trouvé")
    void getParticipantById_ThrowsException_WhenNotFound() {
        // Arrange
        String nonExistingId = "non-existing-id";
        when(participantRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ParticipantNotFoundException.class, () -> {
            participantService.getParticipantById(nonExistingId);
        });
    }

    @Test
    @DisplayName("Récupérer un participant par email")
    void getParticipantByEmail_ReturnsParticipant_WhenFound() {
        // Arrange
        when(participantRepository.findByEmail(participant.getEmail())).thenReturn(Optional.of(participant));

        // Act
        Optional<Participant> result = participantService.getParticipantByEmail(participant.getEmail());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(participant.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Récupérer un participant par email retourne Optional vide si non trouvé")
    void getParticipantByEmail_ReturnsEmpty_WhenNotFound() {
        // Arrange
        when(participantRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<Participant> result = participantService.getParticipantByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Mettre à jour un participant")
    void updateParticipant_Success() throws ParticipantNotFoundException {
        // Arrange
        Participant updatedParticipant = new Participant();
        updatedParticipant.setId(participant.getId());
        updatedParticipant.setNom("Alice Updated");
        updatedParticipant.setEmail("alice.updated@example.com");

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(updatedParticipant);

        // Act
        Participant result = participantService.updateParticipant(participant.getId(), updatedParticipant);

        // Assert
        assertNotNull(result);
        assertEquals("Alice Updated", result.getNom());
        assertEquals("alice.updated@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Mettre à jour un participant inexistant lance une exception")
    void updateParticipant_ThrowsException_WhenNotFound() {
        // Arrange
        String nonExistingId = "non-existing-id";
        Participant updateData = new Participant();
        updateData.setNom("Updated Name");

        when(participantRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ParticipantNotFoundException.class, () -> {
            participantService.updateParticipant(nonExistingId, updateData);
        });

        verify(participantRepository, never()).save(any(Participant.class));
    }

    @Test
    @DisplayName("Suppression d'un participant")
    void deleteParticipant_Success() throws ParticipantNotFoundException {
        // Arrange
        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));
        doNothing().when(participantRepository).delete(any(Participant.class));

        // Act
        participantService.deleteParticipant(participant.getId());

        // Assert
        verify(participantRepository).delete(participant);
    }

    @Test
    @DisplayName("Suppression d'un participant inexistant lance une exception")
    void deleteParticipant_ThrowsException_WhenNotFound() {
        // Arrange
        String nonExistingId = "non-existing-id";
        when(participantRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ParticipantNotFoundException.class, () -> {
            participantService.deleteParticipant(nonExistingId);
        });

        verify(participantRepository, never()).delete(any(Participant.class));
    }

    @Test
    @DisplayName("Rechercher des participants par nom")
    void rechercherParNom_ReturnMatchingParticipants() {
        // Arrange
        List<Participant> matchingParticipants = Arrays.asList(participant);
        when(participantRepository.findByNomContainingIgnoreCase(anyString())).thenReturn(matchingParticipants);

        // Act
        List<Participant> result = participantService.rechercherParNom("Alice");

        // Assert
        assertEquals(1, result.size());
        assertEquals(participant.getId(), result.get(0).getId());
        verify(participantRepository).findByNomContainingIgnoreCase("Alice");
    }

    @Test
    @DisplayName("Rechercher des participants par nom retourne une liste vide si aucun match")
    void rechercherParNom_ReturnsEmptyList_WhenNoMatch() {
        // Arrange
        when(participantRepository.findByNomContainingIgnoreCase("NonExistent")).thenReturn(Arrays.asList());

        // Act
        List<Participant> result = participantService.rechercherParNom("NonExistent");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Créer un organisateur avec succès")
    void creerOrganisateur_Success() {
        // Arrange
        when(participantRepository.save(any(Organisateur.class))).thenReturn(organisateur);

        // Act
        Participant result = participantService.creerParticipant(organisateur);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof Organisateur);
        assertEquals(organisateur.getId(), result.getId());
        assertEquals(organisateur.getNom(), result.getNom());
        verify(participantRepository).save(organisateur);
    }

    @Test
    @DisplayName("Vérifier que l'ID est généré automatiquement pour un nouveau participant")
    void creerParticipant_GeneratesId_WhenNoIdProvided() {
        // Arrange
        Participant newParticipant = new Participant("New User", "new@example.com");
        // Ne pas définir d'ID explicitement

        Participant savedParticipant = new Participant("New User", "new@example.com");
        savedParticipant.setId("auto-generated-id");

        when(participantRepository.save(any(Participant.class))).thenReturn(savedParticipant);

        // Act
        Participant result = participantService.creerParticipant(newParticipant);

        // Assert
        assertNotNull(result.getId());
        assertEquals("auto-generated-id", result.getId());
    }
}