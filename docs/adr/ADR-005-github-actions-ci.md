# ADR-005 — Pipeline CI avec GitHub Actions

**Date :** 2026-04-17  
**Statut :** Accepté

## Contexte

Le projet est hébergé sur GitHub. Sans pipeline d'intégration continue, rien ne garantit qu'une pull request compile correctement ou que les tests passent avant d'être mergée sur `master`. Au fur et à mesure que le projet grandit (tests d'intégration, lint, couverture), cette vérification manuelle devient un risque.

## Décision

Utiliser **GitHub Actions** comme solution de CI, déclenchée à chaque `push` et `pull_request` vers `master`.

## Raisons

- **Natif GitHub** : aucun outil tiers à configurer ou à maintenir. Le workflow vit dans le repo (`.github/workflows/ci.yml`) et est versionné avec le code.
- **Gratuit** : illimité pour les repos publics ; 2 000 minutes/mois sur les repos privés (largement suffisant pour ce projet).
- **Écosystème riche** : actions communautaires prêtes à l'emploi (`setup-java`, `cache`, `upload-artifact`...) qui couvrent tous les besoins sans écrire de scripts custom.
- **Feedback immédiat** : le statut de la CI est visible directement dans l'interface de la PR GitHub, avant tout merge.

## Alternatives écartées

| Outil | Raison du rejet |
|---|---|
| **Jenkins** | Nécessite un serveur auto-hébergé à maintenir — complexité disproportionnée pour un projet solo |
| **CircleCI / Travis CI** | Services tiers avec des plans gratuits plus restrictifs ; moins intégrés à GitHub qu'Actions |
| **GitLab CI** | Non pertinent, le projet est sur GitHub |

## Implémentation

Le workflow (`.github/workflows/ci.yml`) effectue les étapes suivantes :

1. **Checkout** du code source
2. **Setup Java 21** (distribution Temurin / Eclipse Adoptium)
3. **Cache Maven** (`~/.m2/repository`) indexé sur le hash de `pom.xml` — évite de re-télécharger toutes les dépendances à chaque run
4. **`mvn clean verify`** — compile, génère les sources OpenAPI et exécute les tests
5. **Upload du rapport de tests** (`target/surefire-reports/`) en artefact en cas d'échec, pour faciliter le diagnostic

Les variables d'environnement sensibles (JWT, R2, météo) sont injectées avec des valeurs factices directement dans le workflow pour permettre le chargement du contexte Spring sans credentials réels.

## Conséquences

- Chaque PR vers `master` est automatiquement vérifiée — un build cassé ou un test en échec bloque le merge.
- Les futurs tickets de qualité (Testcontainers, REST-assured, Checkstyle, SpotBugs) s'intègreront naturellement en ajoutant des steps au workflow existant.
- Pour la production, les vraies credentials devront être configurées en **GitHub Secrets** (`Settings > Secrets and variables > Actions`) et référencées via `${{ secrets.NOM_DU_SECRET }}`.
