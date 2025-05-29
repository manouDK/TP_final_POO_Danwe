package com.project.POO.observer;


/**
 * Interface définissant l'observateur dans le pattern Observer
 * Les participants implémentent cette interface pour recevoir des notifications
 */
public interface ParticipantObserver {

    /**
     * Méthode appelée par le sujet observable pour mettre à jour l'observateur
     * @param message Le message de notification
     */
    void update(String message);
}

