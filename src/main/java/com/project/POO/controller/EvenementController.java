package com.project.POO.controller;

import com.project.POO.dto.EvenementDto;
import com.project.POO.exception.CapaciteMaxAtteinteException;
import com.project.POO.exception.EvenementDejaExistantException;
import com.project.POO.exception.EvenementNotFoundException;
import com.project.POO.model.Concert;
import com.project.POO.model.Conference;
import com.project.POO.model.Evenement;
import com.project.POO.model.Participant;
import com.project.POO.service.EvenementService;
import com.project.POO.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
@Tag(name = "Événements", description = "API de gestion des événements")
public class EvenementController {

    private final EvenementService evenementService;
    private final ParticipantService participantService;

    @Operation(summary = "Récupérer tous les événements", description = "Retourne la liste de tous les événements")
    @ApiResponse(responseCode = "200", description = "Liste des événements récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<EvenementDto>> getAllEvenements() {
        List<Evenement> evenements = evenementService.getAllEvenements();
        List<EvenementDto> evenementDtos = evenements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(evenementDtos);
    }

    @Operation(summary = "Récupérer un événement par ID", description = "Retourne un événement spécifique par son ID")
    @ApiResponse(responseCode = "200", description = "Événement trouvé")
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<EvenementDto> getEvenementById(
            @Parameter(description = "ID de l'événement", required = true) @PathVariable String id)
            throws EvenementNotFoundException {
        Evenement evenement = evenementService.getEvenementById(id);
        return ResponseEntity.ok(convertToDto(evenement));
    }

    @Operation(summary = "Créer une conférence", description = "Crée une nouvelle conférence")
    @ApiResponse(responseCode = "201", description = "Conférence créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides ou événement déjà existant")
    @PostMapping("/conferences")
    public ResponseEntity<EvenementDto> createConference(
            @Parameter(description = "Données de la conférence", required = true)
            @Valid @RequestBody Conference conference)
            throws EvenementDejaExistantException {
        Evenement createdEvenement = evenementService.creerEvenement(conference);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(createdEvenement));
    }

    @Operation(summary = "Créer un concert", description = "Crée un nouveau concert")
    @ApiResponse(responseCode = "201", description = "Concert créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides ou événement déjà existant")
    @PostMapping("/concerts")
    public ResponseEntity<EvenementDto> createConcert(
            @Parameter(description = "Données du concert", required = true)
            @Valid @RequestBody Concert concert)
            throws EvenementDejaExistantException {
        Evenement createdEvenement = evenementService.creerEvenement(concert);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(createdEvenement));
    }

    @Operation(summary = "Mettre à jour un événement", description = "Met à jour un événement existant")
    @ApiResponse(responseCode = "200", description = "Événement mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    @PutMapping("/{id}")
    public ResponseEntity<EvenementDto> updateEvenement(
            @Parameter(description = "ID de l'événement", required = true) @PathVariable String id,
            @Parameter(description = "Données mises à jour de l'événement", required = true)
            @Valid @RequestBody EvenementDto evenementDto)
            throws EvenementNotFoundException {
        Evenement evenement = convertToEntity(evenementDto);
        Evenement updatedEvenement = evenementService.updateEvenement(id, evenement);
        return ResponseEntity.ok(convertToDto(updatedEvenement));
    }

    @Operation(summary = "Supprimer un événement", description = "Supprime un événement existant")
    @ApiResponse(responseCode = "204", description = "Événement supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(
            @Parameter(description = "ID de l'événement", required = true) @PathVariable String id)
            throws EvenementNotFoundException {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Annuler un événement", description = "Annule un événement existant et notifie les participants")
    @ApiResponse(responseCode = "200", description = "Événement annulé avec succès")
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    @PutMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerEvenement(
            @Parameter(description = "ID de l'événement", required = true) @PathVariable String id)
            throws EvenementNotFoundException {
        evenementService.annulerEvenement(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Inscrire un participant à un événement",
            description = "Ajoute un participant existant à un événement spécifique")
    @ApiResponse(responseCode = "200", description = "Participant inscrit avec succès")
    @ApiResponse(responseCode = "404", description = "Événement ou participant non trouvé")
    @ApiResponse(responseCode = "400", description = "Capacité maximale atteinte")
    @PostMapping("/{evenementId}/participants/{participantId}")
    public ResponseEntity<Void> inscrireParticipant(
            @Parameter(description = "ID de l'événement", required = true) @PathVariable String evenementId,
            @Parameter(description = "ID du participant", required = true) @PathVariable String participantId)
            throws EvenementNotFoundException, CapaciteMaxAtteinteException {
        Participant participant = participantService.getParticipantById(participantId);
        evenementService.ajouterParticipant(evenementId, participant);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Désinscrire un participant d'un événement",
            description = "Retire un participant d'un événement spécifique")
    @ApiResponse(responseCode = "200", description = "Participant désinscrit avec succès")
    @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    @DeleteMapping("/{evenementId}/participants/{participantId}")
    public ResponseEntity<Void> desinscrireParticipant(
            @Parameter(description = "ID de l'événement", required = true) @PathVariable String evenementId,
            @Parameter(description = "ID du participant", required = true) @PathVariable String participantId)
            throws EvenementNotFoundException {
        evenementService.supprimerParticipant(evenementId, participantId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Rechercher des événements par lieu",
            description = "Retourne la liste des événements dont le lieu contient la valeur recherchée")
    @ApiResponse(responseCode = "200", description = "Liste des événements récupérée avec succès")
    @GetMapping("/recherche")
    public ResponseEntity<List<EvenementDto>> rechercherParLieu(
            @Parameter(description = "Lieu à rechercher") @RequestParam String lieu) {
        List<Evenement> evenements = evenementService.rechercherParLieu(lieu);
        List<EvenementDto> evenementDtos = evenements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(evenementDtos);
    }

    @Operation(summary = "Lister les événements disponibles",
            description = "Retourne la liste des événements non annulés et qui ont encore des places disponibles")
    @ApiResponse(responseCode = "200", description = "Liste des événements disponibles récupérée avec succès")
    @GetMapping("/disponibles")
    public ResponseEntity<List<EvenementDto>> evenementsDisponibles() {
        List<Evenement> evenements = evenementService.evenementsDisponibles();
        List<EvenementDto> evenementDtos = evenements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(evenementDtos);
    }

    // Méthodes utilitaires pour la conversion entre entités et DTOs
    private EvenementDto convertToDto(Evenement evenement) {
        if (evenement == null) {
            return null;
        }

        EvenementDto dto = new EvenementDto();
        dto.setId(evenement.getId());
        dto.setNom(evenement.getNom());
        dto.setDate(evenement.getDate());
        dto.setLieu(evenement.getLieu());
        dto.setCapaciteMax(evenement.getCapaciteMax());
        dto.setAnnule(evenement.isAnnule());
        dto.setNombreParticipants(evenement.getParticipants() != null ? evenement.getParticipants().size() : 0);

        if (evenement instanceof Conference) {
            Conference conference = (Conference) evenement;
            dto.setType("CONFERENCE");
            dto.setTheme(conference.getTheme());

            // Gérer la liste des intervenants proprement
            if (conference.getIntervenants() != null) {
                dto.setIntervenants(conference.getIntervenants().stream()
                        .map(intervenant -> intervenant.getId())
                        .collect(Collectors.toList()));
            } else {
                dto.setIntervenants(new ArrayList<>());
            }
        } else if (evenement instanceof Concert) {
            Concert concert = (Concert) evenement;
            dto.setType("CONCERT");
            dto.setArtiste(concert.getArtiste());
            dto.setGenreMusical(concert.getGenreMusical());
        }

        return dto;
    }

    private Evenement convertToEntity(EvenementDto dto) {
        Evenement evenement;

        if ("CONFERENCE".equals(dto.getType())) {
            Conference conference = new Conference();
            conference.setTheme(dto.getTheme());
            // Initialiser la liste des intervenants
            conference.setIntervenants(new ArrayList<>());
            evenement = conference;
        } else if ("CONCERT".equals(dto.getType())) {
            Concert concert = new Concert();
            concert.setArtiste(dto.getArtiste());
            concert.setGenreMusical(dto.getGenreMusical());
            evenement = concert;
        } else {
            throw new IllegalArgumentException("Type d'événement non supporté: " + dto.getType());
        }

        evenement.setId(dto.getId());
        evenement.setNom(dto.getNom());
        evenement.setDate(dto.getDate());
        evenement.setLieu(dto.getLieu());
        evenement.setCapaciteMax(dto.getCapaciteMax());
        evenement.setAnnule(dto.isAnnule());

        // Initialiser la liste des participants
        evenement.setParticipants(new ArrayList<>());

        return evenement;
    }
}