package com.example.apidressing.config;

import com.example.apidressing.gen.model.ClothingLayer;
import com.example.apidressing.gen.model.ItemStatus;
import com.example.apidressing.model.ClothingItemEntity;
import com.example.apidressing.model.UserEntity;
import com.example.apidressing.repository.ClothingItemRepository;
import com.example.apidressing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ClothingItemRepository clothingItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) return;

        UserEntity user = new UserEntity();
        user.setEmail("test@yopmail.com");
        user.setPassword(passwordEncoder.encode("test"));
        user = userRepository.save(user);

        Long userId = user.getId();
        clothingItemRepository.saveAll(java.util.List.of(
                item(userId, "T-shirt blanc",         ClothingLayer.TOP,       1, false, false, ItemStatus.IN_WARDROBE),
                item(userId, "Jean slim bleu",        ClothingLayer.BOTTOM,    1, false, false, ItemStatus.IN_WARDROBE),
                item(userId, "Pull en laine gris",    ClothingLayer.MID,       4, false, true,  ItemStatus.IN_WARDROBE),
                item(userId, "Manteau trench beige",  ClothingLayer.OUTER,     3, true,  true,  ItemStatus.IN_WARDROBE),
                item(userId, "Doudoune noire",        ClothingLayer.OUTER,     5, false, true,  ItemStatus.IN_WARDROBE),
                item(userId, "Chaussettes coton",     ClothingLayer.BASE,      1, false, false, ItemStatus.IN_WARDROBE),
                item(userId, "Sneakers blanches",     ClothingLayer.SHOES,     1, false, false, ItemStatus.IN_WARDROBE),
                item(userId, "Bonnet laine rouge",    ClothingLayer.ACCESSORY, 4, false, false, ItemStatus.WISHLIST),
                item(userId, "Veste en jean",         ClothingLayer.OUTER,     2, false, false, ItemStatus.IN_WARDROBE),
                item(userId, "Short de sport",        ClothingLayer.BOTTOM,    1, false, false, ItemStatus.IN_WARDROBE)
        ));

        log.info("JDD chargé — compte de test : test@dressing.fr / password123");
    }

    private ClothingItemEntity item(Long userId, String name, ClothingLayer layer,
                                    int warmth, boolean waterproof, boolean windproof,
                                    ItemStatus status) {
        ClothingItemEntity e = new ClothingItemEntity();
        e.setUserId(userId);
        e.setName(name);
        e.setLayer(layer);
        e.setWarmthLevel(warmth);
        e.setWaterproof(waterproof);
        e.setWindproof(windproof);
        e.setStatus(status);
        return e;
    }
}
