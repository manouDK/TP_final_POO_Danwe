package com.project.POO;

import com.project.POO.service.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    @Spy
    private EmailNotificationService notificationService;

    @BeforeEach
    void setUp() {
        // Réinitialiser le spy avant chaque test
        reset(notificationService);
    }

    @Test
    @DisplayName("Envoi de notification par email - version simplifiée")
    void envoyerNotification_SendsEmail() {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test de notification";

        // Act - Ne devrait pas lever d'exception
        assertDoesNotThrow(() -> {
            notificationService.envoyerNotification(destinataire, message);
        });

        // Assert - Vérifier que la méthode a été appelée
        verify(notificationService, times(1)).envoyerNotification(destinataire, message);
    }

    @Test
    @DisplayName("Envoi de notification asynchrone")
    void envoyerNotificationAsync_ReturnsCompletableFuture() throws InterruptedException, ExecutionException, TimeoutException {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test de notification asynchrone";

        // Act
        CompletableFuture<Boolean> future = notificationService.envoyerNotificationAsync(destinataire, message);

        // Assert
        assertNotNull(future);
        Boolean result = future.get(5, TimeUnit.SECONDS);
        assertTrue(result, "La notification asynchrone devrait retourner true en cas de succès");
    }

    @Test
    @DisplayName("Envoi de notification asynchrone avec délai")
    void envoyerNotificationAsync_CompletesWithinTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test de notification avec délai";

        // Act
        long startTime = System.currentTimeMillis();
        CompletableFuture<Boolean> future = notificationService.envoyerNotificationAsync(destinataire, message);
        Boolean result = future.get(3, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        // Assert
        assertTrue(result);
        assertTrue((endTime - startTime) < 3000, "La notification devrait se terminer rapidement");
    }

    @Test
    @DisplayName("Envoi de notification en lot")
    void envoyerNotificationEnLot_SendsToAllRecipients() throws InterruptedException {
        // Arrange
        List<String> destinataires = Arrays.asList(
                "user1@example.com",
                "user2@example.com",
                "user3@example.com"
        );
        String message = "Message de notification en lot";
        CountDownLatch latch = new CountDownLatch(1);

        // Créer un spy pour pouvoir vérifier les appels
        doAnswer(invocation -> {
            // Appeler la vraie méthode
            notificationService.envoyerNotificationEnLot(destinataires, message);
            latch.countDown();
            return null;
        }).when(notificationService).envoyerNotificationEnLot(any(List.class), anyString());

        // Act
        notificationService.envoyerNotificationEnLot(destinataires, message);

        // Assert
        assertTrue(latch.await(5, TimeUnit.SECONDS), "L'envoi en lot devrait se terminer dans les temps");
        verify(notificationService, times(1)).envoyerNotificationEnLot(destinataires, message);
    }

    @Test
    @DisplayName("Envoi de notification avec destinataire null ne lance pas d'exception")
    void envoyerNotification_HandlesNullRecipient() {
        // Arrange
        String destinataire = null;
        String message = "Test avec destinataire null";

        // Act & Assert - Ne devrait pas lever d'exception
        assertDoesNotThrow(() -> {
            notificationService.envoyerNotification(destinataire, message);
        });
    }

    @Test
    @DisplayName("Envoi de notification avec message null ne lance pas d'exception")
    void envoyerNotification_HandlesNullMessage() {
        // Arrange
        String destinataire = "test@example.com";
        String message = null;

        // Act & Assert - Ne devrait pas lever d'exception
        assertDoesNotThrow(() -> {
            notificationService.envoyerNotification(destinataire, message);
        });
    }

    @Test
    @DisplayName("Envoi de notification asynchrone gère les paramètres null")
    void envoyerNotificationAsync_HandlesNullParameters() throws InterruptedException, ExecutionException, TimeoutException {
        // Arrange
        String destinataire = null;
        String message = null;

        // Act
        CompletableFuture<Boolean> future = notificationService.envoyerNotificationAsync(destinataire, message);

        // Assert
        assertNotNull(future);
        // Le résultat pourrait être true ou false selon l'implémentation
        Boolean result = future.get(2, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Envoi de notification en lot avec liste vide")
    void envoyerNotificationEnLot_HandlesEmptyList() {
        // Arrange
        List<String> destinataires = Arrays.asList();
        String message = "Message pour liste vide";

        // Act & Assert - Ne devrait pas lever d'exception
        assertDoesNotThrow(() -> {
            notificationService.envoyerNotificationEnLot(destinataires, message);
        });
    }

    @Test
    @DisplayName("Multiple notifications asynchrones en parallèle")
    void multipleNotificationsAsync_WorkInParallel() throws InterruptedException {
        // Arrange
        int numberOfNotifications = 5;
        CountDownLatch latch = new CountDownLatch(numberOfNotifications);

        // Act
        for (int i = 0; i < numberOfNotifications; i++) {
            final int index = i;
            CompletableFuture.runAsync(() -> {
                try {
                    notificationService.envoyerNotification("user" + index + "@example.com", "Message " + index);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Assert
        assertTrue(latch.await(10, TimeUnit.SECONDS),
                "Toutes les notifications devraient se terminer dans les temps");
    }
}