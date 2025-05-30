Bonjour monsieur, ce projet permet de gérer efficacement des événements tels que des conférences et des concerts, avec la possibilité de les créer, modifier, consulter, supprimer, ou annuler, tout en envoyant automatiquement des notifications aux participants concernés. Elle offre également une recherche d’événements par lieu et une liste dynamique des événements encore disponibles, c’est-à-dire non annulés et non complets. Côté participants, l’application prend en charge la création, consultation, modification et suppression de profils, tout en distinguant les participants ordinaires des organisateurs, qui peuvent être assignés à des événements spécifiques. La recherche de participants peut se faire par nom ou par email. Le système d’inscription gère l’ajout ou le retrait d’un participant à un événement, vérifie automatiquement la capacité maximale autorisée, et déclenche des notifications en cas de modifications. Toutes les données sont automatiquement sauvegardées dans des fichiers JSON. L’architecture repose sur des concepts avancés comme le pattern Observer pour notifier les participants en cas de changement d’un événement, une séparation propre via des DTOs pour l’échange de données, et l’abstraction de la persistance grâce à des repositories personnalisés. L’ensemble assure une expérience fluide, cohérente et maintenable, sans dépendre d’une base de données externe.




--

## 🚀 Installation et Exécution

### Prérequis
- JDK 21+
- Maven 3.9.9+
- Un IDE Java (IntelliJ IDEA, Eclipse…)

### Lancer l'application

Pour cela, il faut cloner le projet ensuite, aller dans le fichier index qui est dans ressources/static/index.html

