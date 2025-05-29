package com.project.POO.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implémentation simplifiée du service de notification
 * Cette version simule l'envoi d'emails sans dépendre de Spring Mail
 */
@Service
@Slf4j
@EnableAsync
public class EmailNotificationService implements NotificationService {

    @Override
    @Async
    public void envoyerNotification(String destinataire, String message) {
        log.info("📧 Envoi d'une notification à {}: {}", destinataire, message);

        try {
            // Simulation d'un délai d'envoi d'email
            Thread.sleep(100);

            // Dans une vraie application, ici on enverrait l'email via SMTP
            // Pour le TP, on simule juste l'envoi
            log.info("✅ Notification envoyée avec succès à {}", destinataire);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("❌ Erreur lors de l'envoi de la notification à {}: {}", destinataire, e.getMessage());
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de la notification à {}: {}", destinataire, e.getMessage());
        }
    }

    /**
     * Envoi de notification asynchrone avec retour de statut
     * @param destinataire Email du destinataire
     * @param message Message à envoyer
     * @return CompletableFuture avec le statut d'envoi
     */
    public CompletableFuture<Boolean> envoyerNotificationAsync(String destinataire, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                envoyerNotification(destinataire, message);
                return true;
            } catch (Exception e) {
                log.error("❌ Erreur asynchrone lors de l'envoi à {}: {}", destinataire, e.getMessage());
                return false;
            }
        });
    }

    /**
     * Envoi en lot de notifications
     * @param destinataires Liste des emails
     * @param message Message commun
     */
    public void envoyerNotificationEnLot(java.util.List<String> destinataires, String message) {
        log.info("📧 Envoi en lot de {} notifications", destinataires.size());

        destinataires.forEach(destinataire ->
                CompletableFuture.runAsync(() -> envoyerNotification(destinataire, message))
        );
    }
}