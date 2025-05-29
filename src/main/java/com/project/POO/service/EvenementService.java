package com.project.POO.service;

import com.project.POO.exception.CapaciteMaxAtteinteException;
import com.project.POO.exception.EvenementDejaExistantException;
import com.project.POO.exception.EvenementNotFoundException;
import com.project.POO.model.Evenement;
import com.project.POO.model.Participant;
import com.project.POO.repository.JsonEvenementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final JsonEvenementRepository evenementRepository;
    private final NotificationService notificationService;
    private final GestionEvenements gestionEvenements;

    public Evenement creerEvenement(Evenement evenement) throws EvenementDejaExistantException {
        if (evenementRepository.existsByNomAndDate(evenement.getNom(), evenement.getDate())) {
            throw new EvenementDejaExistantException("Un événement avec le même nom et date existe déjà");
        }

        Evenement savedEvenement = evenementRepository.save(evenement);
        gestionEvenements.ajouterEvenement(savedEvenement);

        return savedEvenement;
    }

    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    public Evenement getEvenementById(String id) throws EvenementNotFoundException {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new EvenementNotFoundException("Événement non trouvé avec l'id: " + id));
    }

    public Evenement updateEvenement(String id, Evenement evenementDetails) throws EvenementNotFoundException {
        Evenement evenement = getEvenementById(id);

        evenement.setNom(evenementDetails.getNom());
        evenement.setDate(evenementDetails.getDate());
        evenement.setLieu(evenementDetails.getLieu());
        evenement.setCapaciteMax(evenementDetails.getCapaciteMax());

        String message = "L'événement " + evenement.getNom() + " a été mis à jour.";
        evenement.notifyObservers(message);

        envoyerNotificationsAsync(evenement.getParticipants(), message);

        return evenementRepository.save(evenement);
    }

    public void deleteEvenement(String id) throws EvenementNotFoundException {
        Evenement evenement = getEvenementById(id);

        String message = "L'événement " + evenement.getNom() + " a été supprimé.";
        evenement.notifyObservers(message);

        gestionEvenements.supprimerEvenement(id);
        evenementRepository.delete(evenement);

        envoyerNotificationsAsync(evenement.getParticipants(), message);
    }

    public void annulerEvenement(String id) throws EvenementNotFoundException {
        Evenement evenement = getEvenementById(id);
        evenement.annuler();

        String message = "L'événement " + evenement.getNom() + " a été annulé.";
        envoyerNotificationsAsync(evenement.getParticipants(), message);

        evenementRepository.save(evenement);
    }

    public void ajouterParticipant(String evenementId, Participant participant)
            throws EvenementNotFoundException, CapaciteMaxAtteinteException {
        Evenement evenement = getEvenementById(evenementId);

        try {
            if (evenement.ajouterParticipant(participant)) {
                evenementRepository.save(evenement);

                String message = "Vous êtes inscrit à l'événement: " + evenement.getNom();
                CompletableFuture.runAsync(() -> notificationService.envoyerNotification(participant.getEmail(), message));
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Capacité maximale")) {
                throw new CapaciteMaxAtteinteException("La capacité maximale de l'événement est atteinte");
            }
            throw new RuntimeException("Erreur lors de l'ajout du participant", e);
        }
    }

    public void supprimerParticipant(String evenementId, String participantId)
            throws EvenementNotFoundException {
        Evenement evenement = getEvenementById(evenementId);

        evenement.getParticipants().stream()
                .filter(p -> p.getId().equals(participantId))
                .findFirst()
                .ifPresent(participant -> {
                    evenement.supprimerParticipant(participant);
                    evenementRepository.save(evenement);

                    // Notification asynchrone
                    String message = "Vous avez été désinscrit de l'événement: " + evenement.getNom();
                    CompletableFuture.runAsync(() ->
                            notificationService.envoyerNotification(participant.getEmail(), message));
                });
    }

    private void envoyerNotificationsAsync(List<Participant> participants, String message) {
        CompletableFuture.runAsync(() -> {
            participants.forEach(participant ->
                    notificationService.envoyerNotification(participant.getEmail(), message));
        });
    }

    // Méthodes utilisant les Streams et lambdas (Java 8+)
    public List<Evenement> rechercherParLieu(String lieu) {
        return evenementRepository.findAll().stream()
                .filter(e -> e.getLieu().toLowerCase().contains(lieu.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Evenement> evenementsDisponibles() {
        return evenementRepository.findAll().stream()
                .filter(e -> !e.isAnnule() && e.getParticipants().size() < e.getCapaciteMax())
                .collect(Collectors.toList());
    }
}