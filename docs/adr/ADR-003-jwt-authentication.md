# ADR-003 — Authentification par JWT

**Date :** 2026-04-12  
**Statut :** Accepté

## Contexte

L'API doit identifier les utilisateurs pour isoler leurs données (garde-robe personnelle). Plusieurs mécanismes d'authentification existent : sessions serveur, OAuth2 délégué, ou tokens JWT auto-porteurs.

## Décision

Utiliser des tokens JWT (JSON Web Token) signés avec HMAC-SHA, émis par l'API elle-même à la connexion et transmis dans le header `Authorization: Bearer <token>` à chaque requête.

## Raisons

- **Stateless** : le serveur ne stocke aucune session — le token contient toutes les informations nécessaires (`userId`, expiration). Cohérent avec une API REST.
- **Simple à mettre en place** : pas de serveur d'autorisation externe (pas d'OAuth2/Keycloak), adapté à un projet sans infrastructure complexe.
- **Portable** : fonctionne naturellement avec des clients mobiles ou front-end SPA qui stockent le token côté client.

## Implémentation

- `JwtService` : génère et valide les tokens (clé HMAC configurée dans `application.properties` via `jwt.secret`, expiration via `jwt.expiration-ms`)
- `JwtAuthenticationFilter` : filtre `OncePerRequestFilter` qui extrait le `userId` du token et l'injecte dans le `SecurityContext`
- `SecurityConfig` : routes `/auth/**` publiques, toutes les autres protégées — politique de session `STATELESS`
- Les mots de passe sont hashés avec BCrypt avant stockage

## Conséquences

- Les tokens ne peuvent pas être révoqués avant expiration (pas de blacklist) — acceptable pour un MVP
- La clé `jwt.secret` doit être changée et gardée secrète en production (la valeur actuelle est une valeur de développement)
- Pour passer à OAuth2 (Google, etc.) plus tard, `JwtAuthenticationFilter` et `SecurityConfig` sont les seuls points à modifier
