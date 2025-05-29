package com.project.POO.service;

import com.project.POO.model.Evenement;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class GestionEvenements {

    private final Map<String, Evenement> evenements;


    public GestionEvenements() {
        this.evenements = new HashMap<>();
    }

    public void ajouterEvenement(Evenement evenement) {
        evenements.put(evenement.getId(), evenement);
    }


    public boolean supprimerEvenement(String id) {
        return evenements.remove(id) != null;
    }



    public Optional<Evenement> rechercherEvenement(String id) {
        return Optional.ofNullable(evenements.get(id));
    }


    public Map<String, Evenement> getEvenements() {
        return Collections.unmodifiableMap(evenements);
    }


}