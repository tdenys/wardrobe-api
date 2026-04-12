package com.example.apidressing.service;

import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.ClothingLayer;
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
        repository.save(new ClothingItem().name("T-shirt Coton Blanc").layer(ClothingLayer.TOP).warmthLevel(1).isWaterproof(false).isWindproof(false));
        repository.save(new ClothingItem().name("Pull en laine").layer(ClothingLayer.TOP).warmthLevel(4).isWaterproof(false).isWindproof(false));
        repository.save(new ClothingItem().name("Chemise").layer(ClothingLayer.TOP).warmthLevel(2).isWaterproof(false).isWindproof(false));
        repository.save(new ClothingItem().name("Jean Brut").layer(ClothingLayer.BOTTOM).warmthLevel(2).isWaterproof(false).isWindproof(false));
        repository.save(new ClothingItem().name("Short en lin").layer(ClothingLayer.BOTTOM).warmthLevel(1).isWaterproof(false).isWindproof(false));
        repository.save(new ClothingItem().name("Trench-coat").layer(ClothingLayer.OUTER).warmthLevel(2).isWaterproof(true).isWindproof(true));
        repository.save(new ClothingItem().name("Doudoune chaude").layer(ClothingLayer.OUTER).warmthLevel(5).isWaterproof(true).isWindproof(true));
        repository.save(new ClothingItem().name("Veste en jean").layer(ClothingLayer.OUTER).warmthLevel(2).isWaterproof(false).isWindproof(false));
        repository.save(new ClothingItem().name("Baskets en toile").layer(ClothingLayer.SHOES).warmthLevel(1).isWaterproof(false).isWindproof(false));
        repository.save(new ClothingItem().name("Bottes en cuir").layer(ClothingLayer.SHOES).warmthLevel(3).isWaterproof(true).isWindproof(false));
    }

    public List<ClothingItem> listItems(ClothingLayer layer) {
        if (layer != null) {
            return repository.findByLayer(layer);
        }
        return repository.findAll();
    }

    public ClothingItem addItem(MultipartFile image, String name, ClothingLayer layer, Integer warmthLevel, String mainColorHex) {
        ClothingItem item = new ClothingItem()
                .name(name)
                .layer(layer)
                .warmthLevel(warmthLevel)
                .isWaterproof(false)
                .isWindproof(false);
        // TODO: stocker l'image et renseigner imageUrl
        return repository.save(item);
    }

    public boolean deleteItem(Long id) {
        return repository.deleteById(id);
    }
}
