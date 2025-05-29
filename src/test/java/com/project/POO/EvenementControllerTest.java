package com.project.POO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.POO.controller.EvenementController;
import com.project.POO.dto.EvenementDto;
import com.project.POO.exception.CapaciteMaxAtteinteException;
import com.project.POO.exception.GlobalExceptionHandler;
import com.project.POO.model.Concert;
import com.project.POO.model.Conference;
import com.project.POO.model.Evenement;
import com.project.POO.model.Participant;
import com.project.POO.service.EvenementService;
import com.project.POO.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EvenementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EvenementService evenementService;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private EvenementController evenementController;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    private Conference conference;
    private Concert concert;
    private Participant participant;
    private EvenementDto conferenceDto;
    private EvenementDto concertDto;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(evenementController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        LocalDateTime futureDate = LocalDateTime.now().plusDays(10);
        conference = new Conference();
        conference.setId("conf-1");
        conference.setNom("TechConf");
        conference.setDate(futureDate);
        conference.setLieu("Salle A");
        conference.setCapaciteMax(100);
        conference.setAnnule(false);
        conference.setParticipants(new ArrayList<>());
        ((Conference) conference).setTheme("Nouvelles technologies");
        ((Conference) conference).setIntervenants(new ArrayList<>());

        concert = new Concert();
        concert.setId("concert-1");
        concert.setNom("LiveMusic");
        concert.setDate(futureDate.plusDays(10));
        concert.setLieu("Canal Olympia");
        concert.setCapaciteMax(1000);
        concert.setAnnule(false);
        concert.setParticipants(new ArrayList<>());
        ((Concert) concert).setArtiste(" fally");
        ((Concert) concert).setGenreMusical("Mbole");

        participant = new Participant();
        participant.setId("part-1");
        participant.setNom("John ");
        participant.setEmail("john@example.com");
        participant.setEvenementsInscrits(new ArrayList<>());

        conferenceDto = new EvenementDto();
        conferenceDto.setId(conference.getId());
        conferenceDto.setNom(conference.getNom());
        conferenceDto.setDate(conference.getDate());
        conferenceDto.setLieu(conference.getLieu());
        conferenceDto.setCapaciteMax(conference.getCapaciteMax());
        conferenceDto.setAnnule(conference.isAnnule());
        conferenceDto.setType("CONFERENCE");
        conferenceDto.setTheme(((Conference) conference).getTheme());
        conferenceDto.setIntervenants(new ArrayList<>());

        concertDto = new EvenementDto();
        concertDto.setId(concert.getId());
        concertDto.setNom(concert.getNom());
        concertDto.setDate(concert.getDate());
        concertDto.setLieu(concert.getLieu());
        concertDto.setCapaciteMax(concert.getCapaciteMax());
        concertDto.setAnnule(concert.isAnnule());
        concertDto.setType("CONCERT");
        concertDto.setArtiste(((Concert) concert).getArtiste());
        concertDto.setGenreMusical(((Concert) concert).getGenreMusical());
    }

    @Test
    @DisplayName("GET /api/evenements - Récupérer tous les événements")
    void getAllEvenements_ReturnsEventsList() throws Exception {
        // Arrange
        List<Evenement> evenements = Arrays.asList(conference, concert);
        when(evenementService.getAllEvenements()).thenReturn(evenements);

        // Act & Assert
        mockMvc.perform(get("/api/evenements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())  // Affiche la requête et la réponse pour faciliter le débogage
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(conference.getId())))
                .andExpect(jsonPath("$[1].id", is(concert.getId())));

        verify(evenementService).getAllEvenements();
    }

    @Test
    @DisplayName("GET /api/evenements/{id} - Récupérer un événement par ID")
    void getEvenementById_ReturnsEvent_WhenFound() throws Exception {
        // Arrange
        when(evenementService.getEvenementById(conference.getId())).thenReturn(conference);

        // Act & Assert
        mockMvc.perform(get("/api/evenements/{id}", conference.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(conference.getId())))
                .andExpect(jsonPath("$.nom", is(conference.getNom())))
                .andExpect(jsonPath("$.type", is("CONFERENCE")));

        verify(evenementService).getEvenementById(conference.getId());
    }

    @Test
    @DisplayName("POST /api/evenements/conferences - Créer une conférence")
    void createConference_ReturnsCreatedConference() throws Exception {
        // Arrange
        when(evenementService.creerEvenement(any(Conference.class))).thenReturn(conference);

        // Act & Assert
        mockMvc.perform(post("/api/evenements/conferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conferenceDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(conference.getId())))
                .andExpect(jsonPath("$.type", is("CONFERENCE")));

        verify(evenementService).creerEvenement(any(Conference.class));
    }

    @Test
    @DisplayName("POST /api/evenements/concerts - Créer un concert")
    void createConcert_ReturnsCreatedConcert() throws Exception {
        // Arrange
        when(evenementService.creerEvenement(any(Concert.class))).thenReturn(concert);

        // Act & Assert
        mockMvc.perform(post("/api/evenements/concerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(concertDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(concert.getId())))
                .andExpect(jsonPath("$.type", is("CONCERT")));

        verify(evenementService).creerEvenement(any(Concert.class));
    }

    @Test
    @DisplayName("PUT /api/evenements/{id} - Mettre à jour un événement")
    void updateEvenement_ReturnsUpdatedEvent() throws Exception {
        // Arrange
        when(evenementService.updateEvenement(eq(conference.getId()), any(Evenement.class))).thenReturn(conference);

        // Act & Assert
        mockMvc.perform(put("/api/evenements/{id}", conference.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conferenceDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(conference.getId())));

        verify(evenementService).updateEvenement(eq(conference.getId()), any(Evenement.class));
    }

    @Test
    @DisplayName("GET /api/evenements/recherche - Rechercher des événements par lieu")
    void rechercherParLieu_ReturnsMatchingEvents() throws Exception {
        // Arrange
        List<Evenement> matchingEvents = Arrays.asList(conference);
        when(evenementService.rechercherParLieu("Salle")).thenReturn(matchingEvents);

        // Act & Assert
        mockMvc.perform(get("/api/evenements/recherche")
                        .param("lieu", "Salle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(conference.getId())));

        verify(evenementService).rechercherParLieu("Salle");
    }

    @Test
    @DisplayName("GET /api/evenements/disponibles - Lister les événements disponibles")
    void evenementsDisponibles_ReturnsAvailableEvents() throws Exception {
        // Arrange
        List<Evenement> availableEvents = Arrays.asList(conference, concert);
        when(evenementService.evenementsDisponibles()).thenReturn(availableEvents);

        // Act & Assert
        mockMvc.perform(get("/api/evenements/disponibles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(evenementService).evenementsDisponibles();
    }


    @Test
    @DisplayName("DELETE /api/evenements/{id} - Supprimer un événement")
    void deleteEvenement_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(evenementService).deleteEvenement(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/evenements/{id}", conference.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /api/evenements/{id}/annuler - Annuler un événement")
    void annulerEvenement_ReturnsOk() throws Exception {
        // Arrange
        doNothing().when(evenementService).annulerEvenement(anyString());

        // Act & Assert
        mockMvc.perform(put("/api/evenements/{id}/annuler", conference.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/evenements/{evenementId}/participants/{participantId} - Inscrire un participant")
    void inscrireParticipant_ReturnsOk() throws Exception {
        // Arrange
        when(participantService.getParticipantById(participant.getId())).thenReturn(participant);
        doNothing().when(evenementService).ajouterParticipant(anyString(), any(Participant.class));

        // Act & Assert
        mockMvc.perform(post("/api/evenements/{evenementId}/participants/{participantId}",
                        conference.getId(), participant.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/evenements/{evenementId}/participants/{participantId} - Retourne 400 quand capacité max atteinte")
    void inscrireParticipant_Returns400_WhenCapacityReached() throws Exception {
        // Arrange
        when(participantService.getParticipantById(participant.getId())).thenReturn(participant);
        doThrow(new CapaciteMaxAtteinteException("Capacité maximale atteinte"))
                .when(evenementService).ajouterParticipant(anyString(), any(Participant.class));

        // Act & Assert
        mockMvc.perform(post("/api/evenements/{evenementId}/participants/{participantId}",
                        conference.getId(), participant.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/evenements/{evenementId}/participants/{participantId} - Désinscrire un participant")
    void desinscrireParticipant_ReturnsOk() throws Exception {
        // Arrange
        doNothing().when(evenementService).supprimerParticipant(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/evenements/{evenementId}/participants/{participantId}",
                        conference.getId(), participant.getId()))
                .andExpect(status().isOk());
    }
}