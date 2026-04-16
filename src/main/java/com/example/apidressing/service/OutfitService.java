package com.example.apidressing.service;

import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.CreateOutfitRequest;
import com.example.apidressing.gen.model.Outfit;
import com.example.apidressing.gen.model.PagedOutfits;
import com.example.apidressing.model.ClothingItemEntity;
import com.example.apidressing.model.OutfitEntity;
import com.example.apidressing.repository.ClothingItemRepository;
import com.example.apidressing.repository.OutfitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutfitService {

    private final OutfitRepository outfitRepository;
    private final ClothingItemRepository clothingItemRepository;

    @Transactional(readOnly = true)
    public PagedOutfits listOutfits(int page, int size) {
        Long userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<OutfitEntity> entityPage = outfitRepository.findByUserId(userId, pageable);

        PagedOutfits result = new PagedOutfits();
        result.setContent(entityPage.getContent().stream().map(this::toDto).toList());
        result.setTotalElements(entityPage.getTotalElements());
        result.setTotalPages(entityPage.getTotalPages());
        result.setPage(entityPage.getNumber());
        result.setSize(entityPage.getSize());
        return result;
    }

    @Transactional
    public Outfit createOutfit(CreateOutfitRequest request) {
        Long userId = getCurrentUserId();

        // Vérifie que tous les vêtements appartiennent bien à l'utilisateur courant
        List<ClothingItemEntity> items = request.getItemIds().stream()
                .map(itemId -> clothingItemRepository.findByIdAndUserId(itemId, userId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Vêtement introuvable : id=" + itemId)))
                .toList();

        OutfitEntity entity = new OutfitEntity();
        entity.setUserId(userId);
        entity.setName(request.getName());
        entity.setItems(items);

        return toDto(outfitRepository.save(entity));
    }

    @Transactional
    public void deleteOutfit(Long id) {
        Long userId = getCurrentUserId();
        OutfitEntity entity = outfitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tenue introuvable : id=" + id));
        outfitRepository.delete(entity);
    }

    // --- Helpers ---

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Outfit toDto(OutfitEntity e) {
        Outfit outfit = new Outfit();
        outfit.setId(e.getId());
        outfit.setName(e.getName());
        outfit.setCreatedAt(e.getCreatedAt().atOffset(ZoneOffset.UTC));
        outfit.setItems(e.getItems().stream().map(this::itemToDto).toList());
        return outfit;
    }

    private ClothingItem itemToDto(ClothingItemEntity e) {
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
