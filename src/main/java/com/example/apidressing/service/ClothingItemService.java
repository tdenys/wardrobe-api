package com.example.apidressing.service;

import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.ClothingLayer;
import com.example.apidressing.model.ClothingItemEntity;
import com.example.apidressing.repository.ClothingItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClothingItemService {

    private final ClothingItemRepository repository;

    /**
     * Pré-charge la garde-robe de démonstration au démarrage.
     */
    @PostConstruct
    void seedWardrobe() {
        if (repository.count() > 0) return;
        repository.save(entity("T-shirt Coton Blanc", ClothingLayer.TOP, 1, false, false));
        repository.save(entity("Pull en laine",       ClothingLayer.TOP, 4, false, false));
        repository.save(entity("Chemise",             ClothingLayer.TOP, 2, false, false));
        repository.save(entity("Jean Brut",           ClothingLayer.BOTTOM, 2, false, false));
        repository.save(entity("Short en lin",        ClothingLayer.BOTTOM, 1, false, false));
        repository.save(entity("Trench-coat",         ClothingLayer.OUTER, 2, true,  true));
        repository.save(entity("Doudoune chaude",     ClothingLayer.OUTER, 5, true,  true));
        repository.save(entity("Veste en jean",       ClothingLayer.OUTER, 2, false, false));
        repository.save(entity("Baskets en toile",    ClothingLayer.SHOES, 1, false, false));
        repository.save(entity("Bottes en cuir",      ClothingLayer.SHOES, 3, true,  false));
    }

    public List<ClothingItem> listItems(ClothingLayer layer) {
        List<ClothingItemEntity> entities = (layer != null)
                ? repository.findByLayer(layer)
                : repository.findAll();
        return entities.stream().map(this::toDto).toList();
    }

    public ClothingItem addItem(MultipartFile image, String name, ClothingLayer layer, Integer warmthLevel, Boolean isWaterproof, Boolean isWindproof, String mainColorHex) {
        ClothingItemEntity e = entity(name, layer, warmthLevel, isWaterproof, isWindproof);
        // TODO: stocker l'image et renseigner imageUrl
        return toDto(repository.save(e));
    }

    public boolean deleteItem(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    // --- Helpers ---

    private ClothingItemEntity entity(String name, ClothingLayer layer, int warmth, boolean waterproof, boolean windproof) {
        ClothingItemEntity e = new ClothingItemEntity();
        e.setName(name);
        e.setLayer(layer);
        e.setWarmthLevel(warmth);
        e.setWaterproof(waterproof);
        e.setWindproof(windproof);
        return e;
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
