# ADR-001 — Base de données H2 en mémoire

**Date :** 2026-04-12  
**Statut :** Accepté

## Contexte

L'application a besoin de persister les vêtements de la garde-robe. Le projet inclut déjà le driver PostgreSQL et Spring Data JPA, mais aucun serveur de base de données n'est configuré. L'objectif immédiat est de valider l'architecture de persistance sans friction d'infrastructure.

## Décision

Utiliser H2 en mode in-memory (`jdbc:h2:mem:dressing`) comme base de données de développement.

## Raisons

- Aucune installation requise — H2 démarre embarqué dans le processus Spring Boot
- Permet de valider le modèle JPA (`ClothingItemEntity`, repositories, mappings) sans dépendance externe
- La console H2 (`/h2-console`) facilite l'inspection des données en développement
- Le passage à PostgreSQL ne nécessite que de changer `application.properties` et la dépendance Maven — le code JPA ne change pas

## Conséquences

- Les données sont perdues à chaque redémarrage
- Non utilisable en production
- Quand la persistance réelle sera nécessaire, basculer vers PostgreSQL (driver déjà présent dans `pom.xml`) en configurant `spring.datasource.url`, `spring.datasource.username` et `spring.datasource.password`
