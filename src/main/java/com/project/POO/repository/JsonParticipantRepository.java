package com.project.POO.repository;

import com.project.POO.model.Participant;
import com.project.POO.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class JsonParticipantRepository {

    private static final String PARTICIPANTS_FILE = "data/participants.json";
    private final Map<String, Participant> participants = new HashMap<>();

    public JsonParticipantRepository() {
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
            if (Files.exists(Paths.get(PARTICIPANTS_FILE))) {
                List<Participant> loadedParticipants = JsonUtils.loadListFromFile(PARTICIPANTS_FILE, Participant.class);
                participants.clear();
                for (Participant participant : loadedParticipants) {
                    participants.put(participant.getId(), participant);
                }
                log.info("Chargement de {} participants depuis le fichier JSON", participants.size());
            }
        } catch (IOException e) {
            log.error("Erreur lors du chargement des participants: {}", e.getMessage());
        }
    }

    private void saveToFile() {
        try {
            List<Participant> participantsList = new ArrayList<>(participants.values());
            JsonUtils.saveToFile(participantsList, PARTICIPANTS_FILE);
            log.debug("Sauvegarde de {} participants dans le fichier JSON", participantsList.size());
        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde des participants: {}", e.getMessage());
        }
    }

    public List<Participant> findAll() {
        return new ArrayList<>(participants.values());
    }

    public Optional<Participant> findById(String id) {
        return Optional.ofNullable(participants.get(id));
    }

    public Participant save(Participant participant) {
        if (participant.getId() == null) {
            participant.setId(UUID.randomUUID().toString());
        }
        participants.put(participant.getId(), participant);
        saveToFile();
        return participant;
    }

    public void delete(Participant participant) {
        participants.remove(participant.getId());
        saveToFile();
    }

    public void deleteById(String id) {
        participants.remove(id);
        saveToFile();
    }

    public boolean existsById(String id) {
        return participants.containsKey(id);
    }

    public Optional<Participant> findByEmail(String email) {
        return participants.values().stream()
                .filter(p -> p.getEmail().equals(email))
                .findFirst();
    }

    public List<Participant> findByNomContainingIgnoreCase(String nom) {
        return participants.values().stream()
                .filter(p -> p.getNom().toLowerCase().contains(nom.toLowerCase()))
                .collect(Collectors.toList());
    }

    public boolean existsByEmail(String email) {
        return participants.values().stream()
                .anyMatch(p -> p.getEmail().equals(email));
    }

    public long count() {
        return participants.size();
    }
}