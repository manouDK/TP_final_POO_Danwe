package com.project.POO.repository;

import com.project.POO.model.Evenement;
import com.project.POO.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class JsonEvenementRepository {

    private static final String EVENTS_FILE = "data/evenements.json";
    private final Map<String, Evenement> evenements = new HashMap<>();

    public JsonEvenementRepository() {
        createDataDirectoryIfNotExists();
        loadFromFile();
    }

    private void createDataDirectoryIfNotExists() {
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            log.error("Erreur lors de la création du répertoire data: {}", e.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            if (Files.exists(Paths.get(EVENTS_FILE))) {
                List<Evenement> loadedEvents = JsonUtils.loadListFromFile(EVENTS_FILE, Evenement.class);
                evenements.clear();
                for (Evenement event : loadedEvents) {
                    evenements.put(event.getId(), event);
                }
                log.info("Chargement de {} événements depuis le fichier JSON", evenements.size());
            }
        } catch (IOException e) {
            log.error("Erreur lors du chargement des événements: {}", e.getMessage());
        }
    }

    private void saveToFile() {
        try {
            List<Evenement> eventsList = new ArrayList<>(evenements.values());
            JsonUtils.saveToFile(eventsList, EVENTS_FILE);
            log.debug("Sauvegarde de {} événements dans le fichier JSON", eventsList.size());
        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde des événements: {}", e.getMessage());
        }
    }

    public List<Evenement> findAll() {
        return new ArrayList<>(evenements.values());
    }

    public Optional<Evenement> findById(String id) {
        return Optional.ofNullable(evenements.get(id));
    }

    public Evenement save(Evenement evenement) {
        if (evenement.getId() == null) {
            evenement.setId(UUID.randomUUID().toString());
        }
        evenements.put(evenement.getId(), evenement);
        saveToFile();
        return evenement;
    }

    public void delete(Evenement evenement) {
        evenements.remove(evenement.getId());
        saveToFile();
    }

    public void deleteById(String id) {
        evenements.remove(id);
        saveToFile();
    }

    public boolean existsById(String id) {
        return evenements.containsKey(id);
    }

    public boolean existsByNomAndDate(String nom, LocalDateTime date) {
        return evenements.values().stream()
                .anyMatch(e -> e.getNom().equals(nom) && e.getDate().equals(date));
    }

    public List<Evenement> findByLieuContainingIgnoreCase(String lieu) {
        return evenements.values().stream()
                .filter(e -> e.getLieu().toLowerCase().contains(lieu.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Evenement> findByAnnuleFalseAndCapaciteMaxGreaterThan(int nombreParticipants) {
        return evenements.values().stream()
                .filter(e -> !e.isAnnule() && e.getCapaciteMax() > nombreParticipants)
                .collect(Collectors.toList());
    }

    public List<Evenement> findByDateAfter(LocalDateTime date) {
        return evenements.values().stream()
                .filter(e -> e.getDate().isAfter(date))
                .collect(Collectors.toList());
    }

    public long count() {
        return evenements.size();
    }
}