package com.example.apidressing.service;

import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.ClothingLayer;
import com.example.apidressing.gen.model.Outfit;
import com.example.apidressing.model.DailyForecast;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class OutfitRecommendationService {

    private static final double RAIN_PROBABILITY_THRESHOLD = 30; // %
    private static final double WIND_SPEED_THRESHOLD = 15; // km/h

    public Outfit recommendOutfit(DailyForecast forecast, List<ClothingItem> allItems) {
        Outfit outfit = new Outfit();
        outfit.setName("Suggestion pour aujourd'hui");
        List<ClothingItem> chosenItems = new ArrayList<>();

        // --- LOGIQUE DE SÉLECTION ---

        // 1. Est-ce qu'il pleut ?
        boolean needsWaterproof = forecast.getPrecipitationProbability() > RAIN_PROBABILITY_THRESHOLD;
        if (needsWaterproof) {
            // On cherche un vêtement d'extérieur (OUTER) imperméable
            findBestItem(allItems, item ->
                item.getLayer() == ClothingLayer.OUTER && item.getIsWaterproof()
            ).ifPresent(chosenItems::add);

            // On cherche des chaussures imperméables
            findBestItem(allItems, item ->
                item.getLayer() == ClothingLayer.SHOES && item.getIsWaterproof()
            ).ifPresent(chosenItems::add);
        }

        // 2. Est-ce qu'il y a du vent ?
        boolean needsWindproof = forecast.getWindSpeed() > WIND_SPEED_THRESHOLD;
        if (needsWindproof && !needsWaterproof) { // Souvent, les imperméables sont aussi coupe-vent
            findBestItem(allItems, item ->
                item.getLayer() == ClothingLayer.OUTER && item.getIsWindproof()
            ).ifPresent(chosenItems::add);
        }

        // 3. Sélection basée sur la température (simplifié)
        // On cherche un haut (TOP) et un bas (BOTTOM)
        // La logique de chaleur est simpliste et peut être améliorée
        int targetWarmth = getTargetWarmthLevel(forecast.getAverageTemperature());

        // Si on n'a pas déjà une couche extérieure, on en cherche une adaptée à la température
        if (chosenItems.stream().noneMatch(i -> i.getLayer() == ClothingLayer.OUTER)) {
            findBestItem(allItems, item ->
                item.getLayer() == ClothingLayer.OUTER && item.getWarmthLevel() >= targetWarmth
            ).ifPresent(chosenItems::add);
        }

        findBestItem(allItems, item ->
            item.getLayer() == ClothingLayer.TOP && item.getWarmthLevel() >= targetWarmth
        ).ifPresent(chosenItems::add);

        findBestItem(allItems, item ->
            item.getLayer() == ClothingLayer.BOTTOM
        ).ifPresent(chosenItems::add);
        
        // Si on n'a pas trouvé de chaussures, on en prend des normales
        if (chosenItems.stream().noneMatch(i -> i.getLayer() == ClothingLayer.SHOES)) {
            findBestItem(allItems, item -> item.getLayer() == ClothingLayer.SHOES).ifPresent(chosenItems::add);
        }

        outfit.setItems(chosenItems);
        return outfit;
    }

    /**
     * Trouve le meilleur vêtement correspondant à un critère.
     * "Meilleur" signifie ici celui avec le plus haut niveau de chaleur.
     */
    private Optional<ClothingItem> findBestItem(List<ClothingItem> items, Predicate<ClothingItem> filter) {
        return items.stream()
            .filter(filter)
            .max(Comparator.comparing(ClothingItem::getWarmthLevel));
    }

    /**
     * Convertit une température en un "niveau de chaleur" cible.
     * C'est une logique très simple qui mériterait d'être affinée.
     */
    private int getTargetWarmthLevel(double temperature) {
        if (temperature < 5) return 5;   // Très froid
        if (temperature < 10) return 4;  // Froid
        if (temperature < 15) return 3;  // Frais
        if (temperature < 20) return 2;  // Doux
        return 1;                        // Chaud
    }
}
