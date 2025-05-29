package com.project.POO.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.project.POO.observer.ParticipantObserver;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Organisateur.class, name = "organisateur")
})
public class Participant implements ParticipantObserver {

    private String id;
    private String nom;
    private String email;

    @JsonIgnore
    private List<Evenement> evenementsInscrits = new ArrayList<>();

    private List<String> notifications = new ArrayList<>();

    public Participant(String nom, String email) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.email = email;
    }


    public void inscrireEvenement(Evenement evenement) throws Exception {
        if (evenement.ajouterParticipant(this)) {
            evenementsInscrits.add(evenement);
        }
    }

    public void desinscrireEvenement(Evenement evenement) {
        if (evenement.supprimerParticipant(this)) {
            evenementsInscrits.remove(evenement);
        }
    }

    @Override
    public void update(String message) {
        // Sauvegarde la notification
        notifications.add(message);

        // Dans une application réelle, on pourrait envoyer un email ou une notification push
        System.out.println("Notification pour " + nom + " (" + email + "): " + message);
    }
}
