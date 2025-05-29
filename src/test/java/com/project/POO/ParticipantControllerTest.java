package com.project.POO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.POO.controller.ParticipantController;
import com.project.POO.dto.ParticipantDto;
import com.project.POO.exception.GlobalExceptionHandler;
import com.project.POO.exception.ParticipantNotFoundException;
import com.project.POO.model.Organisateur;
import com.project.POO.model.Participant;
import com.project.POO.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ParticipantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private ParticipantController participantController;

    private ObjectMapper objectMapper;
    private Participant participant;
    private Organisateur organisateur;
    private ParticipantDto participantDto;
    private ParticipantDto organisateurDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(participantController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        // Créer des objets de test
        participant = new Participant("Alice ", "alice@example.com");
        participant.setId("part-123");
        participant.setEvenementsInscrits(new ArrayList<>());

        organisateur = new Organisateur("Bob ", "bob@example.com");
        organisateur.setId("org-456");
        organisateur.setEvenementsInscrits(new ArrayList<>());
        organisateur.setEvenementsOrganises(new ArrayList<>());

        participantDto = new ParticipantDto();
        participantDto.setId(participant.getId());
        participantDto.setNom(participant.getNom());
        participantDto.setEmail(participant.getEmail());
        participantDto.setOrganisateur(false);
        participantDto.setEvenementsInscrits(new ArrayList<>());

        organisateurDto = new ParticipantDto();
        organisateurDto.setId(organisateur.getId());
        organisateurDto.setNom(organisateur.getNom());
        organisateurDto.setEmail(organisateur.getEmail());
        organisateurDto.setOrganisateur(true);
        organisateurDto.setEvenementsInscrits(new ArrayList<>());
        organisateurDto.setEvenementsOrganises(new ArrayList<>());
    }

    @Test
    @DisplayName("GET /api/participants - Récupérer tous les participants")
    void getAllParticipants_ReturnsParticipantsList() throws Exception {
        // Arrange
        List<Participant> participants = Arrays.asList(participant, organisateur);
        when(participantService.getAllParticipants()).thenReturn(participants);

        // Act & Assert
        mockMvc.perform(get("/api/participants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(participant.getId())))
                .andExpect(jsonPath("$[1].id", is(organisateur.getId())));
    }

    @Test
    @DisplayName("GET /api/participants/{id} - Récupérer un participant par ID")
    void getParticipantById_ReturnsParticipant_WhenFound() throws Exception {
        // Arrange
        when(participantService.getParticipantById(participant.getId())).thenReturn(participant);

        // Act & Assert
        mockMvc.perform(get("/api/participants/{id}", participant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(participant.getId())))
                .andExpect(jsonPath("$.nom", is(participant.getNom())));
    }

    @Test
    @DisplayName("GET /api/participants/{id} - Retourne 404 quand le participant n'est pas trouvé")
    void getParticipantById_Returns404_WhenNotFound() throws Exception {
        // Arrange
        String nonExistingId = "non-existing-id";
        when(participantService.getParticipantById(nonExistingId))
                .thenThrow(new ParticipantNotFoundException("Participant non trouvé avec l'id: " + nonExistingId));

        // Act & Assert
        mockMvc.perform(get("/api/participants/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/participants - Créer un participant")
    void createParticipant_ReturnsCreatedParticipant() throws Exception {
        // Arrange
        when(participantService.creerParticipant(any(Participant.class))).thenReturn(participant);

        // Act & Assert
        mockMvc.perform(post("/api/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(participant.getId())))
                .andExpect(jsonPath("$.nom", is(participant.getNom())))
                .andExpect(jsonPath("$.email", is(participant.getEmail())));
    }

    @Test
    @DisplayName("POST /api/participants/organisateurs - Créer un organisateur")
    void createOrganisateur_ReturnsCreatedOrganisateur() throws Exception {
        // Arrange
        when(participantService.creerParticipant(any(Organisateur.class))).thenReturn(organisateur);

        // Act & Assert
        mockMvc.perform(post("/api/participants/organisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organisateurDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(organisateur.getId())))
                .andExpect(jsonPath("$.organisateur", is(true)));
    }

    @Test
    @DisplayName("PUT /api/participants/{id} - Mettre à jour un participant")
    void updateParticipant_ReturnsUpdatedParticipant() throws Exception {
        // Arrange
        Participant updatedParticipant = new Participant();
        updatedParticipant.setId(participant.getId());
        updatedParticipant.setNom("Alice Updated");
        updatedParticipant.setEmail("alice.updated@example.com");
        updatedParticipant.setEvenementsInscrits(new ArrayList<>());

        when(participantService.updateParticipant(eq(participant.getId()), any(Participant.class))).thenReturn(updatedParticipant);

        ParticipantDto updatedDto = new ParticipantDto();
        updatedDto.setId(updatedParticipant.getId());
        updatedDto.setNom(updatedParticipant.getNom());
        updatedDto.setEmail(updatedParticipant.getEmail());
        updatedDto.setEvenementsInscrits(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(put("/api/participants/{id}", participant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Alice Updated")))
                .andExpect(jsonPath("$.email", is("alice.updated@example.com")));
    }

    @Test
    @DisplayName("DELETE /api/participants/{id} - Supprimer un participant")
    void deleteParticipant_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(participantService).deleteParticipant(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/participants/{id}", participant.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/participants/recherche - Rechercher des participants par nom")
    void rechercherParNom_ReturnsMatchingParticipants() throws Exception {
        // Arrange
        List<Participant> matchingParticipants = Arrays.asList(participant);
        when(participantService.rechercherParNom(anyString())).thenReturn(matchingParticipants);

        // Act & Assert
        mockMvc.perform(get("/api/participants/recherche")
                        .param("nom", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(participant.getId())));
    }
}