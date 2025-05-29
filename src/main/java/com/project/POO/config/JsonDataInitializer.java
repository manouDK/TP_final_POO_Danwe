package com.project.POO.config;

import com.project.POO.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Classe d'initialisation pour créer les fichiers JSON de données
 * au démarrage de l'application
 */
@Component
@Slf4j
public class JsonDataInitializer implements CommandLineRunner {

    private static final String EVENTS_FILE = "data/evenements.json";
    private static final String PARTICIPANTS_FILE = "data/participants.json";

    @Override
    public void run(String... args) throws Exception {
        log.info("Initialisation des fichiers de données JSON...");

        try {
            // Créer les fichiers s'ils n'existent pas
            JsonUtils.createEmptyFileIfNotExists(EVENTS_FILE);
            JsonUtils.createEmptyFileIfNotExists(PARTICIPANTS_FILE);

            log.info("Fichiers de données initialisés avec succès:");
            log.info("- Événements: {}", EVENTS_FILE);
            log.info("- Participants: {}", PARTICIPANTS_FILE);

        } catch (IOException e) {
            log.error("Erreur lors de l'initialisation des fichiers de données: {}", e.getMessage());
            throw new RuntimeException("Impossible d'initialiser les fichiers de données", e);
        }
    }
}