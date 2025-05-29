// === FONCTIONS DE FORMATAGE ===

/**
 * Formate une date en format français
 * @param {Date} date - La date à formater
 * @returns {string} Date formatée (ex: "15 mars 2024")
 */
function formatDate(date) {
    if (!date || !(date instanceof Date)) {
        return 'Date invalide';
    }

    const options = {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    };

    return date.toLocaleDateString('fr-FR', options);
}

/**
 * Formate une heure en format français
 * @param {Date} date - La date contenant l'heure à formater
 * @returns {string} Heure formatée (ex: "14:30")
 */
function formatTime(date) {
    if (!date || !(date instanceof Date)) {
        return 'Heure invalide';
    }

    return date.toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Formate une date et heure complète
 * @param {Date} date - La date à formater
 * @returns {string} Date et heure formatées (ex: "15 mars 2024 à 14:30")
 */
function formatDateTime(date) {
    if (!date || !(date instanceof Date)) {
        return 'Date invalide';
    }

    return `${formatDate(date)} à ${formatTime(date)}`;
}

/**
 * Formate une date relative (ex: "dans 2 jours", "il y a 3 heures")
 * @param {Date} date - La date à comparer avec maintenant
 * @returns {string} Date relative formatée
 */
function formatRelativeDate(date) {
    if (!date || !(date instanceof Date)) {
        return 'Date invalide';
    }

    const now = new Date();
    const diffMs = date.getTime() - now.getTime();
    const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));
    const diffHours = Math.ceil(diffMs / (1000 * 60 * 60));
    const diffMinutes = Math.ceil(diffMs / (1000 * 60));

    if (Math.abs(diffDays) >= 1) {
        if (diffDays > 0) {
            return diffDays === 1 ? 'demain' : `dans ${diffDays} jours`;
        } else {
            return diffDays === -1 ? 'hier' : `il y a ${Math.abs(diffDays)} jours`;
        }
    } else if (Math.abs(diffHours) >= 1) {
        if (diffHours > 0) {
            return diffHours === 1 ? 'dans 1 heure' : `dans ${diffHours} heures`;
        } else {
            return diffHours === -1 ? 'il y a 1 heure' : `il y a ${Math.abs(diffHours)} heures`;
        }
    } else {
        if (diffMinutes > 0) {
            return diffMinutes <= 1 ? 'dans quelques minutes' : `dans ${diffMinutes} minutes`;
        } else {
            return diffMinutes >= -1 ? 'il y a quelques minutes' : `il y a ${Math.abs(diffMinutes)} minutes`;
        }
    }
}

// === FONCTIONS DE VALIDATION ===

/**
 * Valide une adresse email
 * @param {string} email - L'email à valider
 * @returns {boolean} True si l'email est valide
 */
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * Valide qu'une date est dans le futur
 * @param {Date|string} date - La date à valider
 * @returns {boolean} True si la date est dans le futur
 */
function isDateInFuture(date) {
    const inputDate = new Date(date);
    const now = new Date();
    return inputDate > now;
}

/**
 * Valide qu'un nombre est positif
 * @param {number} num - Le nombre à valider
 * @returns {boolean} True si le nombre est positif
 */
function isPositiveNumber(num) {
    return typeof num === 'number' && num > 0;
}

/**
 * Valide qu'une chaîne n'est pas vide
 * @param {string} str - La chaîne à valider
 * @returns {boolean} True si la chaîne n'est pas vide
 */
function isNonEmptyString(str) {
    return typeof str === 'string' && str.trim().length > 0;
}

// === FONCTIONS UTILITAIRES ===

/**
 * Debounce une fonction (évite les appels trop fréquents)
 * @param {Function} func - La fonction à débouncer
 * @param {number} delay - Le délai en millisecondes
 * @returns {Function} La fonction debouncée
 */
function debounce(func, delay) {
    let timeoutId;
    return function (...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(this, args), delay);
    };
}

/**
 * Throttle une fonction (limite la fréquence d'exécution)
 * @param {Function} func - La fonction à throttler
 * @param {number} delay - Le délai minimum entre les exécutions
 * @returns {Function} La fonction throttlée
 */
function throttle(func, delay) {
    let lastExecTime = 0;
    return function (...args) {
        const now = Date.now();
        if (now - lastExecTime >= delay) {
            lastExecTime = now;
            func.apply(this, args);
        }
    };
}

/**
 * Génère un ID unique
 * @returns {string} Un ID unique
 */
function generateUniqueId() {
    return 'id-' + Math.random().toString(36).substr(2, 9) + '-' + Date.now();
}

/**
 * Capitalise la première lettre d'une chaîne
 * @param {string} str - La chaîne à capitaliser
 * @returns {string} La chaîne capitalisée
 */
function capitalize(str) {
    if (!str || typeof str !== 'string') return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

/**
 * Tronque une chaîne à une longueur donnée
 * @param {string} str - La chaîne à tronquer
 * @param {number} length - La longueur maximale
 * @param {string} ending - Le suffixe à ajouter (défaut: "...")
 * @returns {string} La chaîne tronquée
 */
function truncate(str, length, ending = '...') {
    if (!str || typeof str !== 'string') return '';
    if (str.length <= length) return str;
    return str.slice(0, length - ending.length) + ending;
}

/**
 * Génère une couleur aléatoire en hexadécimal
 * @returns {string} Une couleur en format hex (ex: "#ff5733")
 */
function getRandomColor() {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

/**
 * Convertit une chaîne en slug (URL-friendly)
 * @param {string} str - La chaîne à convertir
 * @returns {string} Le slug généré
 */
function slugify(str) {
    return str
        .toLowerCase()
        .trim()
        .replace(/[àáâãäå]/g, 'a')
        .replace(/[èéêë]/g, 'e')
        .replace(/[ìíîï]/g, 'i')
        .replace(/[òóôõö]/g, 'o')
        .replace(/[ùúûü]/g, 'u')
        .replace(/[ç]/g, 'c')
        .replace(/[ñ]/g, 'n')
        .replace(/[^a-z0-9 -]/g, '')
        .replace(/\s+/g, '-')
        .replace(/-+/g, '-');
}

// === GESTION DES NOTIFICATIONS TOAST ===

/**
 * Affiche une notification toast
 * @param {string} message - Le message à afficher
 * @param {string} type - Le type de notification ('success', 'error', 'warning', 'info')
 * @param {number} duration - Durée d'affichage en millisecondes (défaut: 4000)
 */
function showToast(message, type = 'info', duration = 4000) {
    const toast = document.getElementById('toast');
    const toastMessage = document.getElementById('toast-message');

    if (!toast || !toastMessage) {
        console.warn('Éléments toast non trouvés dans le DOM');
        return;
    }

    // Définir le message
    toastMessage.textContent = message;

    // Supprimer les classes de type précédentes
    toast.classList.remove('success', 'error', 'warning', 'info');

    // Ajouter la classe de type appropriée
    toast.classList.add(type);

    // Afficher le toast
    toast.classList.add('show');

    // Masquer le toast après la durée spécifiée
    setTimeout(() => {
        toast.classList.remove('show');
    }, duration);

    // Ajouter une icône selon le type
    let icon = '';
    switch (type) {
        case 'success':
            icon = '✅ ';
            break;
        case 'error':
            icon = '❌ ';
            break;
        case 'warning':
            icon = '⚠️ ';
            break;
        case 'info':
        default:
            icon = 'ℹ️ ';
            break;
    }

    toastMessage.textContent = icon + message;

    console.log(`🍞 Toast ${type.toUpperCase()}: ${message}`);
}

// === GESTION DU STOCKAGE LOCAL ===

/**
 * Sauvegarde des données dans le localStorage
 * @param {string} key - La clé de stockage
 * @param {any} data - Les données à sauvegarder
 */
function saveToLocalStorage(key, data) {
    try {
        const jsonData = JSON.stringify(data);
        localStorage.setItem(key, jsonData);
    } catch (error) {
        console.error('Erreur lors de la sauvegarde dans localStorage:', error);
    }
}

/**
 * Récupère des données du localStorage
 * @param {string} key - La clé de stockage
 * @param {any} defaultValue - Valeur par défaut si la clé n'existe pas
 * @returns {any} Les données récupérées ou la valeur par défaut
 */
function loadFromLocalStorage(key, defaultValue = null) {
    try {
        const jsonData = localStorage.getItem(key);
        return jsonData ? JSON.parse(jsonData) : defaultValue;
    } catch (error) {
        console.error('Erreur lors de la récupération depuis localStorage:', error);
        return defaultValue;
    }
}

/**
 * Supprime une clé du localStorage
 * @param {string} key - La clé à supprimer
 */
function removeFromLocalStorage(key) {
    try {
        localStorage.removeItem(key);
    } catch (error) {
        console.error('Erreur lors de la suppression du localStorage:', error);
    }
}

// === FONCTIONS DOM ===

/**
 * Sélectionne un élément du DOM de manière sécurisée
 * @param {string} selector - Le sélecteur CSS
 * @returns {Element|null} L'élément trouvé ou null
 */
function $(selector) {
    return document.querySelector(selector);
}

/**
 * Sélectionne tous les éléments correspondant au sélecteur
 * @param {string} selector - Le sélecteur CSS
 * @returns {NodeList} La liste des éléments trouvés
 */
function $$(selector) {
    return document.querySelectorAll(selector);
}

/**
 * Crée un élément DOM avec des attributs
 * @param {string} tag - Le nom de la balise
 * @param {Object} attributes - Les attributs à définir
 * @param {string} textContent - Le contenu textuel
 * @returns {Element} L'élément créé
 */
function createElement(tag, attributes = {}, textContent = '') {
    const element = document.createElement(tag);

    // Définir les attributs
    Object.entries(attributes).forEach(([key, value]) => {
        if (key === 'className') {
            element.className = value;
        } else if (key === 'style' && typeof value === 'object') {
            Object.assign(element.style, value);
        } else {
            element.setAttribute(key, value);
        }
    });

    // Définir le contenu textuel
    if (textContent) {
        element.textContent = textContent;
    }

    return element;
}

// === FONCTIONS D'ANIMATION ===

/**
 * Fait défiler la page vers un élément
 * @param {string|Element} target - Le sélecteur ou l'élément cible
 * @param {number} offset - Décalage en pixels (défaut: 0)
 */
function scrollToElement(target, offset = 0) {
    const element = typeof target === 'string' ? $(target) : target;

    if (element) {
        const elementPosition = element.offsetTop - offset;
        window.scrollTo({
            top: elementPosition,
            behavior: 'smooth'
        });
    }
}

/**
 * Anime l'apparition d'un élément
 * @param {Element} element - L'élément à animer
 * @param {string} animation - Le type d'animation ('fadeIn', 'slideIn', etc.)
 */
function animateElement(element, animation = 'fadeIn') {
    if (!element) return;

    element.style.animation = `${animation} 0.5s ease`;

    // Supprimer l'animation après son exécution
    element.addEventListener('animationend', function() {
        element.style.animation = '';
    }, { once: true });
}

// === EXPORT DES FONCTIONS POUR UTILISATION GLOBALE ===

// Rendre les fonctions disponibles globalement
window.formatDate = formatDate;
window.formatTime = formatTime;
window.formatDateTime = formatDateTime;
window.formatRelativeDate = formatRelativeDate;
window.isValidEmail = isValidEmail;
window.isDateInFuture = isDateInFuture;
window.isPositiveNumber = isPositiveNumber;
window.isNonEmptyString = isNonEmptyString;
window.debounce = debounce;
window.throttle = throttle;
window.generateUniqueId = generateUniqueId;
window.capitalize = capitalize;
window.truncate = truncate;
window.getRandomColor = getRandomColor;
window.slugify = slugify;
window.showToast = showToast;
window.saveToLocalStorage = saveToLocalStorage;
window.loadFromLocalStorage = loadFromLocalStorage;
window.removeFromLocalStorage = removeFromLocalStorage;
window.$ = $;
window.$$ = $$;
window.createElement = createElement;
window.scrollToElement = scrollToElement;
window.animateElement = animateElement;

// Fonctions d'initialisation pour les préférences utilisateur
document.addEventListener('DOMContentLoaded', function() {
    // Charger les préférences de thème
    const savedTheme = loadFromLocalStorage('theme', 'light');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-theme');
    }

    // Log de démarrage
    console.log('🔧 Utilitaires chargés et prêts');
});

// Debug: afficher les fonctions utilitaires disponibles
console.log('📚 Fonctions utilitaires disponibles:', {
    formatage: ['formatDate', 'formatTime', 'formatDateTime', 'formatRelativeDate'],
    validation: ['isValidEmail', 'isDateInFuture', 'isPositiveNumber', 'isNonEmptyString'],
    utilitaires: ['debounce', 'throttle', 'generateUniqueId', 'capitalize', 'truncate'],
    dom: ['$', '$$', 'createElement', 'scrollToElement', 'animateElement'],
    storage: ['saveToLocalStorage', 'loadFromLocalStorage', 'removeFromLocalStorage'],
    ui: ['showToast']
});