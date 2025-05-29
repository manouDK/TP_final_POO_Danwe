// Variables globales
let currentSection = 'dashboard';
let allEvents = [];
let allParticipants = [];
let currentEventForRegistration = null;

// Initialisation de l'application
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
    loadInitialData();
});

// Initialisation
function initializeApp() {
    console.log('🚀 Initialisation de G-Event...');

    // Configurer la date minimale pour le formulaire d'événement
    const eventDateInput = document.getElementById('event-date');
    if (eventDateInput) {
        const now = new Date();
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        eventDateInput.min = now.toISOString().slice(0, 16);
    }
}

// Configuration des écouteurs d'événements
function setupEventListeners() {
    // Navigation
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const section = this.dataset.section;
            switchSection(section);
        });
    });

    // Formulaire de création d'événement
    const eventForm = document.getElementById('create-event-form');
    if (eventForm) {
        eventForm.addEventListener('submit', handleCreateEvent);
    }

    // Sélecteur de type d'événement
    const eventTypeSelect = document.getElementById('event-type');
    if (eventTypeSelect) {
        eventTypeSelect.addEventListener('change', toggleEventTypeFields);
    }

    // Formulaire de création de participant
    const participantForm = document.getElementById('create-participant-form');
    if (participantForm) {
        participantForm.addEventListener('submit', handleCreateParticipant);
    }

    // Recherche d'événements
    const searchEvents = document.getElementById('search-events');
    if (searchEvents) {
        searchEvents.addEventListener('input', debounce(handleEventSearch, 300));
    }

    // Recherche de participants
    const searchParticipants = document.getElementById('search-participants');
    if (searchParticipants) {
        searchParticipants.addEventListener('input', debounce(handleParticipantSearch, 300));
    }

    // Fermeture des modales en cliquant en dehors
    const modalOverlay = document.getElementById('modal-overlay');
    if (modalOverlay) {
        modalOverlay.addEventListener('click', function(e) {
            if (e.target === modalOverlay) {
                closeModal();
            }
        });
    }

    // Raccourcis clavier
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeModal();
        }
    });
}

// Chargement des données initiales
async function loadInitialData() {
    showLoading(true);

    try {
        await Promise.all([
            loadEvents(),
            loadParticipants()
        ]);

        updateDashboardStats();
        updateUpcomingEvents();

        console.log('✅ Données initiales chargées');
    } catch (error) {
        console.error('❌ Erreur lors du chargement initial:', error);
        showToast('Erreur lors du chargement des données', 'error');
    } finally {
        showLoading(false);
    }
}

// Chargement des événements
async function loadEvents() {
    try {
        allEvents = await ApiService.getAllEvents();
        renderEvents(allEvents);
        console.log(`📅 ${allEvents.length} événements chargés`);
    } catch (error) {
        console.error('Erreur lors du chargement des événements:', error);
        allEvents = [];
    }
}

// Chargement des participants
async function loadParticipants() {
    try {
        allParticipants = await ApiService.getAllParticipants();
        renderParticipants(allParticipants);
        updateParticipantSelect();
        console.log(`👥 ${allParticipants.length} participants chargés`);
    } catch (error) {
        console.error('Erreur lors du chargement des participants:', error);
        allParticipants = [];
    }
}

// Navigation entre sections
function switchSection(sectionName) {
    // Masquer toutes les sections
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });

    // Afficher la section sélectionnée
    const targetSection = document.getElementById(sectionName);
    if (targetSection) {
        targetSection.classList.add('active');
    }

    // Mettre à jour les boutons de navigation
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    const activeBtn = document.querySelector(`[data-section="${sectionName}"]`);
    if (activeBtn) {
        activeBtn.classList.add('active');
    }

    currentSection = sectionName;

    // Actualiser les données si nécessaire
    if (sectionName === 'dashboard') {
        updateDashboardStats();
        updateUpcomingEvents();
    }
}

// Rendu des événements
function renderEvents(events) {
    const eventsGrid = document.getElementById('events-grid');
    if (!eventsGrid) return;

    if (events.length === 0) {
        eventsGrid.innerHTML = `
            <div class="text-center" style="grid-column: 1 / -1; padding: 3rem;">
                <i class="fas fa-calendar-times" style="font-size: 3rem; color: var(--gray-400); margin-bottom: 1rem;"></i>
                <p style="color: var(--gray-600); font-size: 1.1rem;">Aucun événement trouvé</p>
                <p style="color: var(--gray-500); font-size: 0.9rem;">Créez votre premier événement pour commencer !</p>
            </div>
        `;
        return;
    }

    eventsGrid.innerHTML = events.map(event => createEventCard(event)).join('');
}

// Création d'une carte d'événement
function createEventCard(event) {
    const eventDate = new Date(event.date);
    const isAvailable = !event.annule && event.nombreParticipants < event.capaciteMax;
    const isFull = event.nombreParticipants >= event.capaciteMax;

    let statusClass = 'available';
    let statusText = 'Disponible';

    if (event.annule) {
        statusClass = 'cancelled';
        statusText = 'Annulé';
    } else if (isFull) {
        statusClass = 'full';
        statusText = 'Complet';
    }

    return `
        <div class="event-card" onclick="showEventDetails('${event.id}')">
            <div class="event-header">
                <div class="event-title">${event.nom}</div>
                <span class="event-type ${event.type.toLowerCase()}">${event.type}</span>
            </div>
            <div class="event-body">
                <div class="event-info">
                    <div class="event-info-item">
                        <i class="fas fa-calendar"></i>
                        <span>${formatDate(eventDate)}</span>
                    </div>
                    <div class="event-info-item">
                        <i class="fas fa-clock"></i>
                        <span>${formatTime(eventDate)}</span>
                    </div>
                    <div class="event-info-item">
                        <i class="fas fa-map-marker-alt"></i>
                        <span>${event.lieu}</span>
                    </div>
                    <div class="event-info-item">
                        <i class="fas fa-users"></i>
                        <span>${event.nombreParticipants}/${event.capaciteMax} participants</span>
                    </div>
                    ${event.type === 'CONFERENCE' ? `
                        <div class="event-info-item">
                            <i class="fas fa-lightbulb"></i>
                            <span>${event.theme || 'Thème non défini'}</span>
                        </div>
                    ` : ''}
                    ${event.type === 'CONCERT' ? `
                        <div class="event-info-item">
                            <i class="fas fa-music"></i>
                            <span>${event.artiste} - ${event.genreMusical}</span>
                        </div>
                    ` : ''}
                </div>
            </div>
            <div class="event-actions">
                <span class="event-status ${statusClass}">${statusText}</span>
                ${isAvailable ? `
                    <button class="btn btn-sm btn-primary" onclick="event.stopPropagation(); showRegistrationModal('${event.id}')">
                        <i class="fas fa-user-plus"></i> Inscrire
                    </button>
                ` : ''}
                ${!event.annule ? `
                    <button class="btn btn-sm btn-warning" onclick="event.stopPropagation(); cancelEvent('${event.id}')">
                        <i class="fas fa-ban"></i> Annuler
                    </button>
                ` : ''}
            </div>
        </div>
    `;
}

// Rendu des participants
function renderParticipants(participants) {
    const participantsGrid = document.getElementById('participants-grid');
    if (!participantsGrid) return;

    if (participants.length === 0) {
        participantsGrid.innerHTML = `
            <div class="text-center" style="grid-column: 1 / -1; padding: 3rem;">
                <i class="fas fa-users" style="font-size: 3rem; color: var(--gray-400); margin-bottom: 1rem;"></i>
                <p style="color: var(--gray-600); font-size: 1.1rem;">Aucun participant trouvé</p>
                <p style="color: var(--gray-500); font-size: 0.9rem;">Ajoutez votre premier participant !</p>
            </div>
        `;
        return;
    }

    participantsGrid.innerHTML = participants.map(participant => createParticipantCard(participant)).join('');
}

// Création d'une carte de participant
function createParticipantCard(participant) {
    const initials = participant.nom.split(' ').map(name => name[0]).join('').toUpperCase();
    const eventsCount = participant.evenementsInscrits ? participant.evenementsInscrits.length : 0;

    return `
        <div class="participant-card">
            <div class="participant-header">
                <div class="participant-avatar">${initials}</div>
                <div class="participant-info">
                    <h3>${participant.nom}</h3>
                    <p>${participant.email}</p>
                    ${participant.organisateur ? '<span class="participant-badge">Organisateur</span>' : ''}
                </div>
            </div>
            <div class="participant-body">
                <div class="event-info-item">
                    <i class="fas fa-calendar-check"></i>
                    <span>${eventsCount} événement(s) inscrit(s)</span>
                </div>
                ${participant.organisateur && participant.evenementsOrganises ? `
                    <div class="event-info-item">
                        <i class="fas fa-crown"></i>
                        <span>${participant.evenementsOrganises.length} événement(s) organisé(s)</span>
                    </div>
                ` : ''}
            </div>
        </div>
    `;
}

// Gestion du formulaire de création d'événement
async function handleCreateEvent(e) {
    e.preventDefault();

    const formData = new FormData(e.target);
    const eventType = document.getElementById('event-type').value;

    const eventData = {
        nom: document.getElementById('event-nom').value,
        date: document.getElementById('event-date').value,
        lieu: document.getElementById('event-lieu').value,
        capaciteMax: parseInt(document.getElementById('event-capacite').value),
        type: eventType
    };

    // Ajouter les champs spécifiques selon le type
    if (eventType === 'CONFERENCE') {
        eventData.theme = document.getElementById('event-theme').value;
        eventData.intervenants = [];
    } else if (eventType === 'CONCERT') {
        eventData.artiste = document.getElementById('event-artiste').value;
        eventData.genreMusical = document.getElementById('event-genre').value;
    }

    try {
        showLoading(true);

        let createdEvent;
        if (eventType === 'CONFERENCE') {
            createdEvent = await ApiService.createConference(eventData);
        } else {
            createdEvent = await ApiService.createConcert(eventData);
        }

        showToast('Événement créé avec succès !', 'success');
        e.target.reset();
        toggleEventTypeFields(); // Reset des champs conditionnels

        // Recharger les événements
        await loadEvents();
        updateDashboardStats();

        // Retourner au tableau de bord
        switchSection('events');

    } catch (error) {
        console.error('Erreur lors de la création de l\'événement:', error);
        showToast('Erreur lors de la création de l\'événement', 'error');
    } finally {
        showLoading(false);
    }
}

// Gestion du formulaire de création de participant
async function handleCreateParticipant(e) {
    e.preventDefault();

    const participantData = {
        nom: document.getElementById('participant-nom').value,
        email: document.getElementById('participant-email').value,
        organisateur: document.getElementById('participant-organisateur').checked
    };

    try {
        showLoading(true);

        let createdParticipant;
        if (participantData.organisateur) {
            createdParticipant = await ApiService.createOrganizer(participantData);
        } else {
            createdParticipant = await ApiService.createParticipant(participantData);
        }

        showToast('Participant créé avec succès !', 'success');
        closeModal();

        // Recharger les participants
        await loadParticipants();
        updateDashboardStats();

    } catch (error) {
        console.error('Erreur lors de la création du participant:', error);
        showToast('Erreur lors de la création du participant', 'error');
    } finally {
        showLoading(false);
    }
}

// Affichage/masquage des champs selon le type d'événement
function toggleEventTypeFields() {
    const eventType = document.getElementById('event-type').value;
    const conferenceFields = document.getElementById('conference-fields');
    const concertFields = document.getElementById('concert-fields');

    // Masquer tous les champs conditionnels
    conferenceFields.classList.remove('show');
    concertFields.classList.remove('show');

    // Afficher les champs appropriés
    if (eventType === 'CONFERENCE') {
        conferenceFields.classList.add('show');
        // Rendre le champ thème requis
        document.getElementById('event-theme').required = true;
        document.getElementById('event-artiste').required = false;
        document.getElementById('event-genre').required = false;
    } else if (eventType === 'CONCERT') {
        concertFields.classList.add('show');
        // Rendre les champs concert requis
        document.getElementById('event-artiste').required = true;
        document.getElementById('event-genre').required = true;
        document.getElementById('event-theme').required = false;
    } else {
        // Aucun champ requis si pas de type sélectionné
        document.getElementById('event-theme').required = false;
        document.getElementById('event-artiste').required = false;
        document.getElementById('event-genre').required = false;
    }
}

// Recherche d'événements
function handleEventSearch(e) {
    const searchTerm = e.target.value.toLowerCase().trim();

    if (searchTerm === '') {
        renderEvents(allEvents);
    } else {
        const filteredEvents = allEvents.filter(event =>
            event.lieu.toLowerCase().includes(searchTerm) ||
            event.nom.toLowerCase().includes(searchTerm)
        );
        renderEvents(filteredEvents);
    }
}

// Recherche de participants
function handleParticipantSearch(e) {
    const searchTerm = e.target.value.toLowerCase().trim();

    if (searchTerm === '') {
        renderParticipants(allParticipants);
    } else {
        const filteredParticipants = allParticipants.filter(participant =>
            participant.nom.toLowerCase().includes(searchTerm) ||
            participant.email.toLowerCase().includes(searchTerm)
        );
        renderParticipants(filteredParticipants);
    }
}

// Affichage des détails d'un événement
async function showEventDetails(eventId) {
    try {
        const event = await ApiService.getEventById(eventId);
        const modal = document.getElementById('event-details-modal');
        const content = document.getElementById('event-details-content');

        const eventDate = new Date(event.date);

        content.innerHTML = `
            <div class="event-details">
                <div class="event-detail-header">
                    <h2>${event.nom}</h2>
                    <span class="event-type ${event.type.toLowerCase()}">${event.type}</span>
                </div>

                <div class="event-detail-info">
                    <div class="event-info-item">
                        <i class="fas fa-calendar"></i>
                        <span><strong>Date:</strong> ${formatDate(eventDate)}</span>
                    </div>
                    <div class="event-info-item">
                        <i class="fas fa-clock"></i>
                        <span><strong>Heure:</strong> ${formatTime(eventDate)}</span>
                    </div>
                    <div class="event-info-item">
                        <i class="fas fa-map-marker-alt"></i>
                        <span><strong>Lieu:</strong> ${event.lieu}</span>
                    </div>
                    <div class="event-info-item">
                        <i class="fas fa-users"></i>
                        <span><strong>Participants:</strong> ${event.nombreParticipants}/${event.capaciteMax}</span>
                    </div>
                    ${event.type === 'CONFERENCE' ? `
                        <div class="event-info-item">
                            <i class="fas fa-lightbulb"></i>
                            <span><strong>Thème:</strong> ${event.theme || 'Non défini'}</span>
                        </div>
                    ` : ''}
                    ${event.type === 'CONCERT' ? `
                        <div class="event-info-item">
                            <i class="fas fa-music"></i>
                            <span><strong>Artiste:</strong> ${event.artiste}</span>
                        </div>
                        <div class="event-info-item">
                            <i class="fas fa-guitar"></i>
                            <span><strong>Genre:</strong> ${event.genreMusical}</span>
                        </div>
                    ` : ''}
                    <div class="event-info-item">
                        <i class="fas fa-info-circle"></i>
                        <span><strong>Statut:</strong> ${event.annule ? 'Annulé' : 'Actif'}</span>
                    </div>
                </div>

                <div class="event-actions mt-3">
                    ${!event.annule && event.nombreParticipants < event.capaciteMax ? `
                        <button class="btn btn-primary" onclick="showRegistrationModal('${event.id}'); closeModal();">
                            <i class="fas fa-user-plus"></i> Inscrire un participant
                        </button>
                    ` : ''}
                    ${!event.annule ? `
                        <button class="btn btn-warning" onclick="cancelEvent('${event.id}'); closeModal();">
                            <i class="fas fa-ban"></i> Annuler l'événement
                        </button>
                    ` : ''}
                </div>
            </div>
        `;

        showModal(modal);

    } catch (error) {
        console.error('Erreur lors du chargement des détails:', error);
        showToast('Erreur lors du chargement des détails', 'error');
    }
}

// Affichage du modal d'inscription
function showRegistrationModal(eventId) {
    currentEventForRegistration = eventId;
    const modal = document.getElementById('registration-modal');
    updateParticipantSelect();
    showModal(modal);
}

// Mise à jour de la liste des participants dans le sélecteur
function updateParticipantSelect() {
    const select = document.getElementById('select-participant');
    if (!select) return;

    select.innerHTML = '<option value="">Choisir un participant...</option>';

    allParticipants.forEach(participant => {
        const option = document.createElement('option');
        option.value = participant.id;
        option.textContent = `${participant.nom} (${participant.email})`;
        select.appendChild(option);
    });
}

// Inscription d'un participant à un événement
async function registerParticipant() {
    const participantId = document.getElementById('select-participant').value;

    if (!participantId || !currentEventForRegistration) {
        showToast('Veuillez sélectionner un participant', 'warning');
        return;
    }

    try {
        showLoading(true);

        await ApiService.registerParticipant(currentEventForRegistration, participantId);

        showToast('Participant inscrit avec succès !', 'success');
        closeModal();

        // Recharger les événements
        await loadEvents();
        updateDashboardStats();

    } catch (error) {
        console.error('Erreur lors de l\'inscription:', error);
        showToast('Erreur lors de l\'inscription', 'error');
    } finally {
        showLoading(false);
    }
}

// Annulation d'un événement
async function cancelEvent(eventId) {
    if (!confirm('Êtes-vous sûr de vouloir annuler cet événement ?')) {
        return;
    }

    try {
        showLoading(true);

        await ApiService.cancelEvent(eventId);

        showToast('Événement annulé avec succès', 'success');

        // Recharger les événements
        await loadEvents();
        updateDashboardStats();

    } catch (error) {
        console.error('Erreur lors de l\'annulation:', error);
        showToast('Erreur lors de l\'annulation', 'error');
    } finally {
        showLoading(false);
    }
}

// Mise à jour des statistiques du tableau de bord
function updateDashboardStats() {
    const totalEvents = allEvents.length;
    const totalParticipants = allParticipants.length;
    const availableEvents = allEvents.filter(event =>
        !event.annule && event.nombreParticipants < event.capaciteMax
    ).length;
    const totalOrganizers = allParticipants.filter(participant =>
        participant.organisateur
    ).length;

    // Mettre à jour les éléments du DOM
    const elements = {
        'total-events': totalEvents,
        'total-participants': totalParticipants,
        'available-events': availableEvents,
        'total-organizers': totalOrganizers
    };

    Object.entries(elements).forEach(([id, value]) => {
        const element = document.getElementById(id);
        if (element) {
            animateCounter(element, value);
        }
    });
}

// Mise à jour des événements à venir
function updateUpcomingEvents() {
    const upcomingEventsContainer = document.getElementById('upcoming-events');
    if (!upcomingEventsContainer) return;

    const now = new Date();
    const upcomingEvents = allEvents
        .filter(event => new Date(event.date) > now && !event.annule)
        .sort((a, b) => new Date(a.date) - new Date(b.date))
        .slice(0, 5);

    if (upcomingEvents.length === 0) {
        upcomingEventsContainer.innerHTML = `
            <p class="text-muted">Aucun événement à venir</p>
        `;
        return;
    }

    upcomingEventsContainer.innerHTML = upcomingEvents
        .map(event => {
            const eventDate = new Date(event.date);
            return `
                <div class="event-item" onclick="showEventDetails('${event.id}')">
                    <h4>${event.nom}</h4>
                    <p>
                        <i class="fas fa-calendar"></i> ${formatDate(eventDate)} à ${formatTime(eventDate)}<br>
                        <i class="fas fa-map-marker-alt"></i> ${event.lieu}<br>
                        <i class="fas fa-users"></i> ${event.nombreParticipants}/${event.capaciteMax} participants
                    </p>
                </div>
            `;
        })
        .join('');
}

// Affichage des modales
function showModal(modal) {
    const overlay = document.getElementById('modal-overlay');

    // Masquer toutes les modales
    document.querySelectorAll('.modal').forEach(m => m.classList.remove('show'));

    // Afficher la modale spécifiée
    modal.classList.add('show');
    overlay.classList.add('show');

    // Focus sur le premier élément focusable
    setTimeout(() => {
        const firstInput = modal.querySelector('input, select, button');
        if (firstInput) {
            firstInput.focus();
        }
    }, 100);
}

// Fermeture des modales
function closeModal() {
    const overlay = document.getElementById('modal-overlay');
    overlay.classList.remove('show');

    document.querySelectorAll('.modal').forEach(modal => {
        modal.classList.remove('show');
    });

    // Réinitialiser les formulaires
    document.querySelectorAll('.modal form').forEach(form => {
        form.reset();
    });

    currentEventForRegistration = null;
}

// Affichage des modales pour création
function showCreateEventModal() {
    switchSection('create-event');
}

function showCreateParticipantModal() {
    const modal = document.getElementById('create-participant-modal');
    showModal(modal);
}

// Affichage/masquage du spinner de chargement
function showLoading(show) {
    const loading = document.getElementById('loading');
    if (loading) {
        if (show) {
            loading.classList.remove('hidden');
        } else {
            loading.classList.add('hidden');
        }
    }
}

// Animation des compteurs
function animateCounter(element, targetValue) {
    const startValue = parseInt(element.textContent) || 0;
    const duration = 1000; // 1 seconde
    const startTime = Date.now();

    function updateCounter() {
        const currentTime = Date.now();
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);

        // Utiliser une fonction d'easing pour une animation plus fluide
        const easedProgress = easeOutCubic(progress);
        const currentValue = Math.round(startValue + (targetValue - startValue) * easedProgress);

        element.textContent = currentValue;

        if (progress < 1) {
            requestAnimationFrame(updateCounter);
        }
    }

    requestAnimationFrame(updateCounter);
}

// Fonction d'easing
function easeOutCubic(t) {
    return 1 - Math.pow(1 - t, 3);
}

// Export des fonctions globales pour les utiliser dans le HTML
window.showEventDetails = showEventDetails;
window.showRegistrationModal = showRegistrationModal;
window.showCreateEventModal = showCreateEventModal;
window.showCreateParticipantModal = showCreateParticipantModal;
window.registerParticipant = registerParticipant;
window.cancelEvent = cancelEvent;
window.closeModal = closeModal;