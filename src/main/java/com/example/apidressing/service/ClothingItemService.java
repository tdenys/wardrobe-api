package com.example.apidressing.service;

import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.ClothingLayer;
import com.example.apidressing.model.ClothingItemEntity;
import com.example.apidressing.repository.ClothingItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClothingItemService {

    private final ClothingItemRepository repository;

    public List<ClothingItem> listItems(ClothingLayer layer) {
        Long userId = getCurrentUserId();
        List<ClothingItemEntity> entities = (layer != null)
                ? repository.findByUserIdAndLayer(userId, layer)
                : repository.findByUserId(userId);
        return entities.stream().map(this::toDto).toList();
    }

    public ClothingItem addItem(MultipartFile image, String name, ClothingLayer layer, Integer warmthLevel, Boolean isWaterproof, Boolean isWindproof, String mainColorHex) {
        ClothingItemEntity e = new ClothingItemEntity();
        e.setUserId(getCurrentUserId());
        e.setName(name);
        e.setLayer(layer);
        e.setWarmthLevel(warmthLevel);
        e.setWaterproof(isWaterproof);
        e.setWindproof(isWindproof);
        // TODO: stocker l'image et renseigner imageUrl
        return toDto(repository.save(e));
    }

    public boolean deleteItem(Long id) {
        Long userId = getCurrentUserId();
        if (!repository.existsByIdAndUserId(id, userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
        return true;
    }

    // --- Helpers ---

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private ClothingItem toDto(ClothingItemEntity e) {
        return new ClothingItem()
                .id(e.getId())
                .name(e.getName())
                .layer(e.getLayer())
                .warmthLevel(e.getWarmthLevel())
                .isWaterproof(e.getWaterproof())
                .isWindproof(e.getWindproof())
                .status(e.getStatus());
    }
}
