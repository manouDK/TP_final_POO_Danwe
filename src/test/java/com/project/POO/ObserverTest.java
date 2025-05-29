package com.project.POO;

import com.project.POO.model.Conference;
import com.project.POO.model.Evenement;
import com.project.POO.model.Participant;
import com.project.POO.observer.ParticipantObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ObserverTest {

    private Evenement evenement;
    private Participant participant1;
    private Participant participant2;

    @Mock
    private ParticipantObserver mockObserver;

    @BeforeEach
    void setUp() {
        // Créer un événement et des participants pour les tests
        evenement = new Conference("Conf IA", LocalDateTime.now().plusDays(10), " S1", 100, "Nouvelles technologies sur IA");
        evenement.setId("evt-1");

        participant1 = new Participant("Alice", "alice@example.com");
        participant1.setId("p1");

        participant2 = new Participant("Bob", "bob@example.com");
        participant2.setId("p2");
    }

    @Test
    @DisplayName("Abonner un participant aux notifications d'un événement")
    void subscribe_AddsParticipantToObservers() {
        // Act
        evenement.subscribe(participant1);
        boolean containsParticipant = evenement.getObservers().contains(participant1);

        // Assert
        assertTrue(containsParticipant);
    }

    @Test
    @DisplayName("Désabonner un participant des notifications d'un événement")
    void unsubscribe_RemovesParticipantFromObservers() {
        // Arrange
        evenement.subscribe(participant1);

        // Act
        evenement.unsubscribe(participant1);

        // Assert
        assertFalse(evenement.getObservers().contains(participant1));
    }

    @Test
    @DisplayName("Notifier tous les participants abonnés")
    void notifyObservers_SendsMessageToAllObservers() {
        // Arrange
        evenement.subscribe(mockObserver);
        String message = "L'événement a été modifié";

        // Act
        evenement.notifyObservers(message);

        // Assert
        verify(mockObserver, times(1)).update(message);
    }

    @Test
    @DisplayName("Pas de notification aux participants non abonnés")
    void notifyObservers_DoesNotSendMessageToUnsubscribedObservers() {
        // Arrange
        evenement.subscribe(mockObserver);
        evenement.unsubscribe(mockObserver);
        String message = "L'événement a été modifié";

        // Act
        evenement.notifyObservers(message);

        // Assert
        verify(mockObserver, never()).update(anyString());
    }

    @Test
    @DisplayName("Annuler un événement envoie une notification aux participants")
    void annuler_NotifiesAllObservers() {
        // Arrange
        evenement.subscribe(mockObserver);

        // Act
        evenement.annuler();

        // Assert
        verify(mockObserver).update(anyString());
        assertTrue(evenement.isAnnule());
    }

    @Test
    @DisplayName("Ajouter un participant à un événement l'abonne automatiquement")
    void ajouterParticipant_SubscribesParticipantToObservers() throws Exception {
        // Act
        evenement.ajouterParticipant(participant1);

        // Assert
        assertTrue(evenement.getObservers().contains(participant1));
        assertTrue(evenement.getParticipants().contains(participant1));
    }

    @Test
    @DisplayName("Supprimer un participant d'un événement le désabonne automatiquement")
    void supprimerParticipant_UnsubscribesParticipantFromObservers() throws Exception {
        // Arrange
        evenement.ajouterParticipant(participant1);

        // Act
        evenement.supprimerParticipant(participant1);

        // Assert
        assertFalse(evenement.getObservers().contains(participant1));
        assertFalse(evenement.getParticipants().contains(participant1));
    }

    @Test
    @DisplayName("Un participant reçoit des notifications")
    void participant_ReceivesNotifications() {
        // Arrange
        String message = "Test de notification";

        // Act
        participant1.update(message);

        // Assert
        assertTrue(participant1.getNotifications().contains(message));
        assertEquals(1, participant1.getNotifications().size());
    }
}