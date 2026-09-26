# CV-Forge

Application moderne de génération de CV optimisé ATS avec architecture découplée **Spring Boot 3** (Java 21) et **Angular 18**, **Spring AI** (OpenAI *structured output*), base de données **PostgreSQL 16** (stockage hybride JSONB), moteur de rendu vectoriel **Thymeleaf + OpenHTMLtoPDF** et conteneurisation **Docker**.

## Lancement Rapide (Docker Compose)

1. *(Optionnel)* Personnaliser les identifiants et la clé OpenAI à partir du gabarit :
```bash
cp .env.example .env
```

2. Compiler et démarrer l'ensemble des services en arrière-plan :
```bash
docker compose up --build -d
```

Pour arrêter les services :
```bash
docker compose down
```

Pour consulter les logs en temps réel :
```bash
docker compose logs -f
```

### Accès aux Services

| Service | URL |
|---|---|
| **Application Web (Frontend)** | [http://localhost:4200](http://localhost:4200) |
| **API REST (Backend)** | [http://localhost:8080](http://localhost:8080) |
| **Endpoint Génération IA** | `POST http://localhost:8080/api/v1/cv/generate` |
| **Exportation PDF ATS** | `GET http://localhost:8080/api/v1/cv/{id}/pdf` |


## Documentation

Le rapport d'architecture complet est disponible dans le dossier `doc/` :

- **Rapport PDF** : [`doc/rapport.pdf`](doc/rapport.pdf)
- **Source LaTeX** : [`doc/rapport.tex`](doc/rapport.tex)
