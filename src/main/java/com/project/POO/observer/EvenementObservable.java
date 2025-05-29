package com.project.POO.observer;


/**
 * Interface définissant le sujet observable dans le pattern Observer
 * Les événements implémentent cette interface pour pouvoir notifier les participants
 */
public interface EvenementObservable {

    /**
     * Abonne un observateur aux mises à jour de l'événement
     * @param observer L'observateur à abonner
     */
    void subscribe(ParticipantObserver observer);

    /**
     * Désabonne un observateur des mises à jour de l'événement
     * @param observer L'observateur à désabonner
     */
    void unsubscribe(ParticipantObserver observer);

    /**
     * Notifie tous les observateurs abonnés avec un message
     * @param message Le message à envoyer aux observateurs
     */
    void notifyObservers(String message);
}
