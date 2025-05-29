package com.project.POO.service;


/**
 * Interface pour les services de notification
 * Définit le contrat pour l'envoi de notifications aux participants
 */
public interface NotificationService {

    /**
     * Envoie une notification à un destinataire
     * @param destinataire L'adresse email du destinataire
     * @param message Le contenu du message à envoyer
     */
    void envoyerNotification(String destinataire, String message);
}
