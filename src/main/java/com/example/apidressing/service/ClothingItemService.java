package com.example.apidressing.service;

import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.ClothingLayer;
import com.example.apidressing.model.ClothingItemEntity;
import com.example.apidressing.repository.ClothingItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClothingItemService {

    private final ClothingItemRepository repository;
    private final R2StorageService r2StorageService;

    @Value("${r2.public-base-url}")
    private String r2PublicBaseUrl;

    public List<ClothingItem> listItems(ClothingLayer layer) {
        Long userId = getCurrentUserId();
        List<ClothingItemEntity> entities = (layer != null)
                ? repository.findByUserIdAndLayer(userId, layer)
                : repository.findByUserId(userId);
        return entities.stream().map(this::toDto).toList();
    }

    public ClothingItem addItem(MultipartFile image, String name, ClothingLayer layer, Integer warmthLevel, Boolean isWaterproof, Boolean isWindproof, String mainColorHex) {
        ClothingItemEntity e = new ClothingItemEntity();
        Long userId = getCurrentUserId();
        e.setUserId(userId);
        e.setName(name);
        e.setLayer(layer);
        e.setWarmthLevel(warmthLevel);
        e.setWaterproof(isWaterproof);
        e.setWindproof(isWindproof);

        if (image != null && !image.isEmpty()) {
            String extension = resolveExtension(image);
            String key = "clothing-items/" + userId + "/" + UUID.randomUUID() + "." + extension;
            try {
                r2StorageService.uploadFile(key, image);
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Échec de l'upload de l'image", ex);
            }
            e.setImageUrl(r2PublicBaseUrl + "/" + key);
        }

        return toDto(repository.save(e));
    }

    public boolean deleteItem(Long id) {
        Long userId = getCurrentUserId();
        ClothingItemEntity entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Fix #3 : supprime l'image dans R2 avant de supprimer l'entrée en base
        if (entity.getImageUrl() != null) {
            String key = extractR2Key(entity.getImageUrl());
            try {
                r2StorageService.deleteFile(key);
            } catch (Exception ex) {
                // On logue sans bloquer la suppression de l'article
                log.warn("Impossible de supprimer l'image R2 pour la clé '{}' : {}", key, ex.getMessage());
            }
        }

        repository.deleteById(id);
        return true;
    }

    // --- Helpers ---

    /**
     * Extrait la clé R2 (chemin dans le bucket) à partir de l'URL publique.
     * Ex: "https://pub-xxx.r2.dev/clothing-items/1/uuid.jpg" -> "clothing-items/1/uuid.jpg"
     */
    private String extractR2Key(String imageUrl) {
        String prefix = r2PublicBaseUrl.endsWith("/") ? r2PublicBaseUrl : r2PublicBaseUrl + "/";
        return imageUrl.startsWith(prefix) ? imageUrl.substring(prefix.length()) : imageUrl;
    }

    private String resolveExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        }
        return "jpg";
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private ClothingItem toDto(ClothingItemEntity e) {
        // Fix #1 : évite un NullPointerException quand l'article n'a pas d'image
        URI imageUri = (e.getImageUrl() != null) ? URI.create(e.getImageUrl()) : null;

        return new ClothingItem()
                .id(e.getId())
                .name(e.getName())
                .imageUrl(imageUri)
                .layer(e.getLayer())
                .warmthLevel(e.getWarmthLevel())
                .isWaterproof(e.getWaterproof())
                .isWindproof(e.getWindproof())
                .status(e.getStatus());
    }
}
