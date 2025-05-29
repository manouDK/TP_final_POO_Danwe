// Configuration de l'API
const API_CONFIG = {
    BASE_URL: 'http://localhost:8080/api',
    HEADERS: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    TIMEOUT: 10000 // 10 secondes
};

// Service API pour gérer toutes les requêtes
class ApiService {

    // Méthode générique pour les requêtes HTTP
    static async makeRequest(endpoint, options = {}) {
        const url = `${API_CONFIG.BASE_URL}${endpoint}`;

        const defaultOptions = {
            headers: API_CONFIG.HEADERS,
            timeout: API_CONFIG.TIMEOUT
        };

        const requestOptions = { ...defaultOptions, ...options };

        // Ajouter un timeout personnalisé
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), requestOptions.timeout);
        requestOptions.signal = controller.signal;

        try {
            console.log(`🌐 ${requestOptions.method || 'GET'} ${url}`);

            const response = await fetch(url, requestOptions);
            clearTimeout(timeoutId);

            // Vérifier si la réponse est OK
            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                throw new ApiError(
                    response.status,
                    errorData?.message || `HTTP ${response.status}: ${response.statusText}`,
                    errorData
                );
            }

            // Si la réponse est vide (204 No Content), retourner null
            if (response.status === 204) {
                return null;
            }

            // Parser la réponse JSON
            const data = await response.json();
            console.log(`✅ Réponse reçue:`, data);

            return data;

        } catch (error) {
            clearTimeout(timeoutId);

            if (error.name === 'AbortError') {
                throw new ApiError(0, 'Timeout: La requête a pris trop de temps', null);
            }

            if (error instanceof ApiError) {
                throw error;
            }

            // Erreur réseau ou autre
            console.error(`❌ Erreur API:`, error);
            throw new ApiError(0, 'Erreur de connexion au serveur', null);
        }
    }

    // === ÉVÉNEMENTS ===

    // Récupérer tous les événements
    static async getAllEvents() {
        return await this.makeRequest('/evenements');
    }

    // Récupérer un événement par ID
    static async getEventById(id) {
        return await this.makeRequest(`/evenements/${id}`);
    }

    // Créer une conférence
    static async createConference(conferenceData) {
        return await this.makeRequest('/evenements/conferences', {
            method: 'POST',
            body: JSON.stringify(conferenceData)
        });
    }

    // Créer un concert
    static async createConcert(concertData) {
        return await this.makeRequest('/evenements/concerts', {
            method: 'POST',
            body: JSON.stringify(concertData)
        });
    }

    // Mettre à jour un événement
    static async updateEvent(id, eventData) {
        return await this.makeRequest(`/evenements/${id}`, {
            method: 'PUT',
            body: JSON.stringify(eventData)
        });
    }

    // Supprimer un événement
    static async deleteEvent(id) {
        return await this.makeRequest(`/evenements/${id}`, {
            method: 'DELETE'
        });
    }

    // Annuler un événement
    static async cancelEvent(id) {
        return await this.makeRequest(`/evenements/${id}/annuler`, {
            method: 'PUT'
        });
    }

    // Rechercher des événements par lieu
    static async searchEventsByLocation(location) {
        const params = new URLSearchParams({ lieu: location });
        return await this.makeRequest(`/evenements/recherche?${params}`);
    }

    // Récupérer les événements disponibles
    static async getAvailableEvents() {
        return await this.makeRequest('/evenements/disponibles');
    }

    // Inscrire un participant à un événement
    static async registerParticipant(eventId, participantId) {
        return await this.makeRequest(`/evenements/${eventId}/participants/${participantId}`, {
            method: 'POST'
        });
    }

    // Désinscrire un participant d'un événement
    static async unregisterParticipant(eventId, participantId) {
        return await this.makeRequest(`/evenements/${eventId}/participants/${participantId}`, {
            method: 'DELETE'
        });
    }

    // === PARTICIPANTS ===

    // Récupérer tous les participants
    static async getAllParticipants() {
        return await this.makeRequest('/participants');
    }

    // Récupérer un participant par ID
    static async getParticipantById(id) {
        return await this.makeRequest(`/participants/${id}`);
    }

    // Créer un participant
    static async createParticipant(participantData) {
        return await this.makeRequest('/participants', {
            method: 'POST',
            body: JSON.stringify(participantData)
        });
    }

    // Créer un organisateur
    static async createOrganizer(organizerData) {
        return await this.makeRequest('/participants/organisateurs', {
            method: 'POST',
            body: JSON.stringify(organizerData)
        });
    }

    // Mettre à jour un participant
    static async updateParticipant(id, participantData) {
        return await this.makeRequest(`/participants/${id}`, {
            method: 'PUT',
            body: JSON.stringify(participantData)
        });
    }

    // Supprimer un participant
    static async deleteParticipant(id) {
        return await this.makeRequest(`/participants/${id}`, {
            method: 'DELETE'
        });
    }

    // Rechercher des participants par nom
    static async searchParticipantsByName(name) {
        const params = new URLSearchParams({ nom: name });
        return await this.makeRequest(`/participants/recherche?${params}`);
    }

    // === MÉTHODES UTILITAIRES ===

    // Vérifier l'état de santé de l'API
    static async checkHealth() {
        try {
            const response = await this.makeRequest('/test/health');
            return { status: 'OK', response };
        } catch (error) {
            return { status: 'ERROR', error: error.message };
        }
    }

    // Obtenir les statistiques générales
    static async getStats() {
        try {
            const [events, participants] = await Promise.all([
                this.getAllEvents(),
                this.getAllParticipants()
            ]);

            const availableEvents = events.filter(event =>
                !event.annule && event.nombreParticipants < event.capaciteMax
            );

            const organizers = participants.filter(participant =>
                participant.organisateur
            );

            const upcomingEvents = events.filter(event =>
                new Date(event.date) > new Date() && !event.annule
            );

            return {
                totalEvents: events.length,
                totalParticipants: participants.length,
                availableEvents: availableEvents.length,
                totalOrganizers: organizers.length,
                upcomingEvents: upcomingEvents.length,
                cancelledEvents: events.filter(e => e.annule).length
            };

        } catch (error) {
            console.error('Erreur lors du calcul des statistiques:', error);
            throw error;
        }
    }

    // Méthode pour tester la connectivité
    static async testConnection() {
        const startTime = Date.now();

        try {
            await this.checkHealth();
            const duration = Date.now() - startTime;

            console.log(`🟢 Connexion API OK (${duration}ms)`);
            return { connected: true, duration };

        } catch (error) {
            const duration = Date.now() - startTime;

            console.error(`🔴 Connexion API échouée (${duration}ms):`, error.message);
            return { connected: false, duration, error: error.message };
        }
    }
}

// Classe d'erreur personnalisée pour l'API
class ApiError extends Error {
    constructor(status, message, data = null) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.data = data;
    }

    // Vérifier si l'erreur est due à un problème réseau
    isNetworkError() {
        return this.status === 0;
    }

    // Vérifier si l'erreur est du côté client (4xx)
    isClientError() {
        return this.status >= 400 && this.status < 500;
    }

    // Vérifier si l'erreur est du côté serveur (5xx)
    isServerError() {
        return this.status >= 500;
    }

    // Obtenir un message d'erreur user-friendly
    getUserMessage() {
        switch (this.status) {
            case 0:
                return 'Impossible de se connecter au serveur. Vérifiez votre connexion.';
            case 400:
                return this.data?.message || 'Données invalides.';
            case 401:
                return 'Vous n\'êtes pas autorisé à effectuer cette action.';
            case 403:
                return 'Accès interdit.';
            case 404:
                return 'Ressource non trouvée.';
            case 409:
                return this.data?.message || 'Conflit: la ressource existe déjà.';
            case 500:
                return 'Erreur interne du serveur.';
            default:
                return this.message || 'Une erreur inattendue s\'est produite.';
        }
    }
}

// Intercepteur global pour les erreurs API
window.addEventListener('unhandledrejection', function(event) {
    if (event.reason instanceof ApiError) {
        console.error('Erreur API non gérée:', event.reason);

        // Afficher une notification d'erreur à l'utilisateur
        if (typeof showToast === 'function') {
            showToast(event.reason.getUserMessage(), 'error');
        }

        // Empêcher l'affichage de l'erreur dans la console du navigateur
        event.preventDefault();
    }
});

// Test de connexion automatique au chargement
document.addEventListener('DOMContentLoaded', async function() {
    // Attendre un peu pour que l'UI soit prête
    setTimeout(async () => {
        const connectionResult = await ApiService.testConnection();

        if (!connectionResult.connected) {
            if (typeof showToast === 'function') {
                showToast(
                    'Impossible de se connecter au serveur. Assurez-vous que l\'application Spring Boot est démarrée.',
                    'error'
                );
            }
        }
    }, 1000);
});

// Export de la classe pour utilisation globale
window.ApiService = ApiService;
window.ApiError = ApiError;