# ADR-002 — Approche contract-first avec OpenAPI Generator

**Date :** 2026-04-12  
**Statut :** Accepté

## Contexte

L'API doit exposer des endpoints REST cohérents et documentés. Deux approches existent : écrire le code d'abord et générer la doc ensuite (code-first), ou écrire le contrat d'abord et générer le code ensuite (contract-first).

## Décision

Adopter l'approche contract-first : le fichier `src/main/resources/contract/smart-wardrobe-api.yaml` est la source de vérité. Le plugin Maven `openapi-generator-maven-plugin` génère à la compilation l'interface `WardrobeApi` et les modèles (`ClothingItem`, `Outfit`, etc.) dans `target/generated-sources/openapi/`.

## Raisons

- Le contrat YAML est lisible par tous (front-end, mobile, testeurs) sans lire le code Java
- L'interface générée force le contrôleur à implémenter tous les endpoints définis — un oubli est une erreur de compilation
- Modifier le contrat et recompiler suffit pour propager un changement d'API jusqu'au contrôleur
- Les modèles de réponse sont cohérents avec la documentation sans effort de synchronisation

## Conséquences

- Les classes générées (`target/generated-sources/`) ne doivent jamais être éditées manuellement — elles sont écrasées à chaque `mvn compile`
- Les annotations JPA ne peuvent pas être ajoutées aux modèles générés → nécessite des entités séparées (voir `ClothingItemEntity`)
- Tout changement de signature dans le YAML casse la compilation du contrôleur jusqu'à mise à jour de l'`@Override` concerné
