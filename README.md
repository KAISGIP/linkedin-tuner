# LinkedIn Tuner

Une application web pour optimiser et publier des posts LinkedIn avec l'aide de l'IA.

## Fonctionnalités

- Interface utilisateur moderne et intuitive
- Édition et prévisualisation des posts
- Optimisation du contenu avec IA
- Publication directe sur LinkedIn
- Authentification sécurisée

## Prérequis

- Java 17 ou supérieur
- Node.js 16 ou supérieur
- MongoDB
- Compte développeur LinkedIn avec les permissions nécessaires

## Configuration

### Backend (Spring Boot)

1. Cloner le repository
2. Configurer les variables d'environnement dans `src/main/resources/application.properties` :
   ```properties
   linkedin.client.id=votre_client_id
   linkedin.client.secret=votre_client_secret
   linkedin.api.url=https://api.linkedin.com/v2
   linkedin.person.id=votre_person_id
   ```

3. Démarrer le serveur :
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend (React)

1. Installer les dépendances :
   ```bash
   cd frontend
   npm install
   ```

2. Démarrer l'application :
   ```bash
   npm start
   ```

## Utilisation

1. Accédez à l'application via `http://localhost:3000`
2. Connectez-vous avec les identifiants suivants :
   - Email : admin@example.com
   - Mot de passe : admin
3. Créez et optimisez vos posts LinkedIn
4. Publiez directement sur votre profil

## Sécurité

- Authentification basée sur Spring Security
- Protection des routes API
- Gestion sécurisée des tokens LinkedIn
- Validation des entrées utilisateur

## Structure du Projet

```
linkedin-tuner/
├── src/                    # Code source backend
│   ├── main/
│   │   ├── java/
│   │   └── resources/
├── frontend/              # Code source frontend
│   ├── public/
│   └── src/
└── pom.xml               # Configuration Maven
```

## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :
1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commiter vos changements
4. Pousser vers la branche
5. Ouvrir une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails. 