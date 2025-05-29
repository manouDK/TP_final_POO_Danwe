package com.project.POO.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Classe utilitaire pour la sérialisation et désérialisation JSON
 * avec support pour l'héritage de classes
 */
@Component
public class JsonUtils {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Configuration pour supporter l'héritage
        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.project.POO.model")
                .build();
        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);

        // Configuration pour une meilleure lisibilité
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Convertit un objet en chaîne JSON
     * @param object L'objet à convertir
     * @return La chaîne JSON
     * @throws JsonProcessingException En cas d'erreur de sérialisation
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Convertit une chaîne JSON en objet
     * @param json La chaîne JSON
     * @param clazz La classe de l'objet
     * @param <T> Le type de l'objet
     * @return L'objet désérialisé
     * @throws JsonProcessingException En cas d'erreur de désérialisation
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * Sauvegarde un objet dans un fichier JSON
     * @param object L'objet à sauvegarder
     * @param filePath Le chemin du fichier
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void saveToFile(Object object, String filePath) throws IOException {
        File file = new File(filePath);
        // Créer le répertoire parent s'il n'existe pas
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
    }

    /**
     * Charge un objet depuis un fichier JSON
     * @param filePath Le chemin du fichier
     * @param clazz La classe de l'objet
     * @param <T> Le type de l'objet
     * @return L'objet chargé
     * @throws IOException En cas d'erreur de lecture
     */
    public static <T> T loadFromFile(String filePath, Class<T> clazz) throws IOException {
        return objectMapper.readValue(new File(filePath), clazz);
    }

    /**
     * Charge une liste d'objets depuis un fichier JSON
     * @param filePath Le chemin du fichier
     * @param listType La classe des objets dans la liste
     * @param <T> Le type des objets dans la liste
     * @return La liste d'objets chargée
     * @throws IOException En cas d'erreur de lecture
     */
    public static <T> List<T> loadListFromFile(String filePath, Class<T> listType) throws IOException {
        return objectMapper.readValue(
                new File(filePath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, listType)
        );
    }

    /**
     * Vérifie si un fichier JSON existe
     * @param filePath Le chemin du fichier
     * @return true si le fichier existe, false sinon
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Crée un fichier vide s'il n'existe pas
     * @param filePath Le chemin du fichier
     * @throws IOException En cas d'erreur de création
     */
    public static void createEmptyFileIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            // Créer un fichier avec une liste vide
            objectMapper.writeValue(file, List.of());
        }
    }
}