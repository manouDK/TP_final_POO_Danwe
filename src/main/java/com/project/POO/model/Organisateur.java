package com.project.POO.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Organisateur extends Participant {

    private List<Evenement> evenementsOrganises = new ArrayList<>();

    public Organisateur(String nom, String email) {
        super(nom, email);
    }

    public void organiserEvenement(Evenement evenement) {
        if (!evenementsOrganises.contains(evenement)) {
            evenementsOrganises.add(evenement);
            evenement.setOrganisateur(this);
        }
    }

    public void annulerEvenement(Evenement evenement) {
        if (evenementsOrganises.contains(evenement)) {
            evenement.annuler();
        }
    }
}
