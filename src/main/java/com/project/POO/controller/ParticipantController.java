package com.project.POO.controller;

import com.project.POO.dto.ParticipantDto;
import com.project.POO.exception.ParticipantNotFoundException;
import com.project.POO.model.Organisateur;
import com.project.POO.model.Participant;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
@Tag(name = "Participants", description = "API de gestion des participants")
public class ParticipantController {

    private final ParticipantService participantService;

    @Operation(summary = "Récupérer tous les participants", description = "Retourne la liste de tous les participants")
    @ApiResponse(responseCode = "200", description = "Liste des participants récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<ParticipantDto>> getAllParticipants() {
        List<Participant> participants = participantService.getAllParticipants();
        List<ParticipantDto> participantDtos = participants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(participantDtos);
    }

    @Operation(summary = "Récupérer un participant par ID", description = "Retourne un participant spécifique par son ID")
    @ApiResponse(responseCode = "200", description = "Participant trouvé")
    @ApiResponse(responseCode = "404", description = "Participant non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantDto> getParticipantById(
            @Parameter(description = "ID du participant", required = true) @PathVariable String id)
            throws ParticipantNotFoundException {
        Participant participant = participantService.getParticipantById(id);
        return ResponseEntity.ok(convertToDto(participant));
    }

    @Operation(summary = "Créer un participant", description = "Crée un nouveau participant")
    @ApiResponse(responseCode = "201", description = "Participant créé avec succès")
    @PostMapping
    public ResponseEntity<ParticipantDto> createParticipant(
            @Parameter(description = "Données du participant", required = true)
            @Valid @RequestBody ParticipantDto participantDto) {
        Participant participant = convertToEntity(participantDto);
        Participant createdParticipant = participantService.creerParticipant(participant);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(createdParticipant));
    }

    @Operation(summary = "Créer un organisateur", description = "Crée un nouvel organisateur")
    @ApiResponse(responseCode = "201", description = "Organisateur créé avec succès")
    @PostMapping("/organisateurs")
    public ResponseEntity<ParticipantDto> createOrganisateur(
            @Parameter(description = "Données de l'organisateur", required = true)
            @Valid @RequestBody ParticipantDto organisateurDto) {
        Organisateur organisateur = new Organisateur(organisateurDto.getNom(), organisateurDto.getEmail());
        Participant createdOrganisateur = participantService.creerParticipant(organisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(createdOrganisateur));
    }

    @Operation(summary = "Mettre à jour un participant", description = "Met à jour un participant existant")
    @ApiResponse(responseCode = "200", description = "Participant mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Participant non trouvé")
    @PutMapping("/{id}")
    public ResponseEntity<ParticipantDto> updateParticipant(
            @Parameter(description = "ID du participant", required = true) @PathVariable String id,
            @Parameter(description = "Données mises à jour du participant", required = true)
            @Valid @RequestBody ParticipantDto participantDto)
            throws ParticipantNotFoundException {
        Participant participant = convertToEntity(participantDto);
        Participant updatedParticipant = participantService.updateParticipant(id, participant);
        return ResponseEntity.ok(convertToDto(updatedParticipant));
    }

    @Operation(summary = "Supprimer un participant", description = "Supprime un participant existant")
    @ApiResponse(responseCode = "204", description = "Participant supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Participant non trouvé")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(
            @Parameter(description = "ID du participant", required = true) @PathVariable String id)
            throws ParticipantNotFoundException {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rechercher des participants par nom",
            description = "Retourne la liste des participants dont le nom contient la valeur recherchée")
    @ApiResponse(responseCode = "200", description = "Liste des participants récupérée avec succès")
    @GetMapping("/recherche")
    public ResponseEntity<List<ParticipantDto>> rechercherParNom(
            @Parameter(description = "Nom à rechercher") @RequestParam String nom) {
        List<Participant> participants = participantService.rechercherParNom(nom);
        List<ParticipantDto> participantDtos = participants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(participantDtos);
    }

    // Méthodes utilitaires pour la conversion entre entités et DTOs
    private ParticipantDto convertToDto(Participant participant) {
        ParticipantDto dto = new ParticipantDto();
        dto.setId(participant.getId());
        dto.setNom(participant.getNom());
        dto.setEmail(participant.getEmail());

        // Ajouter les IDs des événements inscrits
        List<String> evenementsIds = participant.getEvenementsInscrits().stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());
        dto.setEvenementsInscrits(evenementsIds);

        // Vérifier si c'est un organisateur et ajouter les événements organisés
        if (participant instanceof Organisateur) {
            dto.setOrganisateur(true);
            Organisateur organisateur = (Organisateur) participant;
            List<String> evenementsOrganisesIds = organisateur.getEvenementsOrganises().stream()
                    .map(e -> e.getId())
                    .collect(Collectors.toList());
            dto.setEvenementsOrganises(evenementsOrganisesIds);
        } else {
            dto.setOrganisateur(false);
        }

        return dto;
    }

    private Participant convertToEntity(ParticipantDto dto) {
        Participant participant;

        if (dto.isOrganisateur()) {
            participant = new Organisateur();
        } else {
            participant = new Participant();
        }

        if (dto.getId() != null) {
            participant.setId(dto.getId());
        }

        participant.setNom(dto.getNom());
        participant.setEmail(dto.getEmail());

        return participant;
    }
}