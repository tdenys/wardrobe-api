# ADR-004 — Stockage des images avec Cloudflare R2

**Date :** 2026-04-14  
**Statut :** Accepté

## Contexte

L'API permet aux utilisateurs d'associer une photo à chaque vêtement (`POST /clothing-items`). Ces images doivent être stockées de façon persistante et accessibles publiquement via une URL stable. La base de données H2 in-memory n'est pas adaptée au stockage binaire, et stocker les images sur le disque local rendrait l'application difficile à déployer.

## Décision

Utiliser **Cloudflare R2** comme service de stockage d'objets pour les images de vêtements, via le SDK AWS S3 (`software.amazon.awssdk:s3`).

## Raisons

- **Compatible S3** : R2 expose une API S3-compatible, ce qui permet d'utiliser le SDK AWS standard sans dépendance propriétaire Cloudflare. Un éventuel changement vers AWS S3 ou un autre fournisseur ne nécessiterait que de modifier `application.properties`.
- **Coût** : R2 ne facture pas les frais de sortie réseau (*egress*), contrairement à AWS S3. Pour une API servant des images fréquemment, cela représente une économie significative.
- **URL publique** : chaque bucket R2 peut être exposé via un sous-domaine `pub-*.r2.dev`, ce qui permet de retourner directement une URL accessible dans la réponse JSON sans proxy intermédiaire.
- **Simplicité d'intégration** : la configuration se limite à un endpoint, un `access-key-id`, un `secret-access-key` et un nom de bucket — tous externalisés en variables d'environnement.

## Implémentation

- `R2Config` instancie un `S3Client` avec `endpointOverride` pointant vers le bucket R2 et `forcePathStyle(true)`.
- `R2StorageService` expose deux méthodes : `uploadFile(key, file)` et `deleteFile(key)`.
- `ClothingItemService` génère une clé de la forme `clothing-items/{userId}/{uuid}.{ext}` à l'upload, et la reconstruit depuis l'URL publique à la suppression.
- La suppression de l'image R2 est tentée avant la suppression en base. Une erreur R2 est loguée (`WARN`) mais ne bloque pas la suppression de l'article.

## Conséquences

- Les images persistent indépendamment du cycle de vie de l'application (contrairement à H2).
- Les credentials R2 (`R2_ACCESS_KEY_ID`, `R2_SECRET_ACCESS_KEY`) doivent être fournis via des variables d'environnement — ne jamais les committer.
- Les images des articles supprimés sont nettoyées automatiquement. En cas d'échec du nettoyage R2 (panne réseau, etc.), un objet orphelin peut subsister dans le bucket — à surveiller via les logs `WARN`.
- Le champ `image` du formulaire multipart est optionnel : un article peut être créé sans photo (`imageUrl` sera `null`).
- En production, envisager de restreindre l'accès public au bucket et de passer par un domaine personnalisé (ex: `images.monapp.com`).
