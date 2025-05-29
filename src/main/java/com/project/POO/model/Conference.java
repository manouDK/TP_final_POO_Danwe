package com.project.POO.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Conference extends Evenement {

    private String theme;
    private List<Participant> intervenants = new ArrayList<>();

    public Conference(String nom, LocalDateTime date, String lieu, int capaciteMax, String theme) {
        super(nom, date, lieu, capaciteMax);
        this.theme = theme;
    }


    public void ajouterIntervenant(Participant intervenant) {
        if (!intervenants.contains(intervenant)) {
            intervenants.add(intervenant);
            notifyObservers("Nouvel intervenant ajouté à la conférence: " + intervenant.getNom());
        }
    }

    public void supprimerIntervenant(Participant intervenant) {
        if (intervenants.remove(intervenant)) {
            notifyObservers("L'intervenant " + intervenant.getNom() + " a été retiré de la conférence.");
        }
    }

    @Override
    public String afficherDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Conférence: ").append(getNom())
                .append("\nDate: ").append(getDate())
                .append("\nLieu: ").append(getLieu())
                .append("\nThème: ").append(theme)
                .append("\nCapacité maximale: ").append(getCapaciteMax())
                .append("\nParticipants inscrits: ").append(getParticipants().size());



        return details.toString();
    }
}