package com.example.apidressing.controller;

import com.example.apidressing.gen.api.WardrobeApi;
import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.ClothingLayer;
import com.example.apidressing.gen.model.Outfit;
import com.example.apidressing.gen.model.WeatherSuggestion;
import com.example.apidressing.model.DailyForecast;
import com.example.apidressing.service.OutfitRecommendationService;
import com.example.apidressing.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WardrobeController implements WardrobeApi {

    private final WeatherService weatherService;
    private final OutfitRecommendationService recommendationService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return WardrobeApi.super.getRequest();
    }

    @Override
    public ResponseEntity<List<ClothingItem>> listClothingItems(ClothingLayer layer) {
        // TODO: Implémenter la récupération depuis la base de données
        return new ResponseEntity<>(getMockWardrobe(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ClothingItem> addClothingItem(MultipartFile image, String name, ClothingLayer layer, Integer warmthLevel, String mainColorHex) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteClothingItem(Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<WeatherSuggestion> getDailySuggestionByGps(Float lat, Float lon) {
        DailyForecast forecast = weatherService.getDailyForecast(lat, lon);
        return buildSuggestionResponse(forecast);
    }

    @Override
    public ResponseEntity<WeatherSuggestion> getDailySuggestionByCity(String city) {
        DailyForecast forecast = weatherService.getDailyForecast(city);
        return buildSuggestionResponse(forecast);
    }

    private ResponseEntity<WeatherSuggestion> buildSuggestionResponse(DailyForecast forecast) {
        if (forecast == null) {
            // Cela peut arriver si la ville n'est pas trouvée ou si l'API météo échoue
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 2. Récupérer les vêtements de l'utilisateur
        // TODO: Remplacer cette liste statique par un appel à la base de données
        List<ClothingItem> userWardrobe = getMockWardrobe();

        // 3. Obtenir la recommandation de tenue
        Outfit recommendedOutfit = recommendationService.recommendOutfit(forecast, userWardrobe);

        // 4. Construire la réponse
        WeatherSuggestion suggestion = new WeatherSuggestion();
        suggestion.setTemperature(Float.valueOf((float) forecast.getAverageTemperature()));
        suggestion.setDescription(forecast.getWeatherCondition());
        suggestion.setSuggestedOutfit(recommendedOutfit);
        suggestion.setReasoning(buildReasoning(forecast));

        return new ResponseEntity<>(suggestion, HttpStatus.OK);
    }

    /**
     * Construit une phrase expliquant pourquoi la tenue a été choisie.
     */
    private String buildReasoning(DailyForecast forecast) {
        List<String> reasons = new ArrayList<>();
        if (forecast.getPrecipitationProbability() > 30) {
            reasons.add("il y a un risque de pluie");
        }
        if (forecast.getWindSpeed() > 15) {
            reasons.add("il y a du vent");
        }
        reasons.add(String.format("la température moyenne est de %.0f°C", Double.valueOf(forecast.getAverageTemperature())));
        return "Tenue suggérée car " + String.join(" et ", reasons) + ".";
    }

    /**
     * Méthode temporaire pour simuler une garde-robe.
     * À remplacer par une vraie source de données.
     */
    private List<ClothingItem> getMockWardrobe() {
        return List.of(
                new ClothingItem().id(Long.valueOf(1L)).name("T-shirt Coton Blanc").layer(ClothingLayer.TOP).warmthLevel(Integer.valueOf(1)).isWaterproof(Boolean.valueOf(false)).isWindproof(Boolean.valueOf(false)),
                new ClothingItem().id(Long.valueOf(2L)).name("Pull en laine").layer(ClothingLayer.TOP).warmthLevel(Integer.valueOf(4)).isWaterproof(Boolean.valueOf(false)).isWindproof(Boolean.valueOf(false)),
                new ClothingItem().id(Long.valueOf(3L)).name("Chemise").layer(ClothingLayer.TOP).warmthLevel(Integer.valueOf(2)).isWaterproof(Boolean.valueOf(false)).isWindproof(Boolean.valueOf(false)),
                new ClothingItem().id(Long.valueOf(4L)).name("Jean Brut").layer(ClothingLayer.BOTTOM).warmthLevel(Integer.valueOf(2)).isWaterproof(Boolean.valueOf(false)).isWindproof(Boolean.valueOf(false)),
                new ClothingItem().id(Long.valueOf(5L)).name("Short en lin").layer(ClothingLayer.BOTTOM).warmthLevel(Integer.valueOf(1)).isWaterproof(Boolean.valueOf(false)).isWindproof(Boolean.valueOf(false)),
                new ClothingItem().id(Long.valueOf(6L)).name("Trench-coat").layer(ClothingLayer.OUTER).warmthLevel(Integer.valueOf(2)).isWaterproof(Boolean.valueOf(true)).isWindproof(Boolean.valueOf(true)),
                new ClothingItem().id(Long.valueOf(7L)).name("Doudoune chaude").layer(ClothingLayer.OUTER).warmthLevel(Integer.valueOf(5)).isWaterproof(Boolean.valueOf(true)).isWindproof(Boolean.valueOf(true)),
                new ClothingItem().id(Long.valueOf(8L)).name("Veste en jean").layer(ClothingLayer.OUTER).warmthLevel(Integer.valueOf(2)).isWaterproof(Boolean.valueOf(false)).isWindproof(Boolean.valueOf(false)),
                new ClothingItem().id(Long.valueOf(9L)).name("Baskets en toile").layer(ClothingLayer.SHOES).warmthLevel(Integer.valueOf(1)).isWaterproof(Boolean.valueOf(false)).isWindproof(Boolean.valueOf(false)),
                new ClothingItem().id(Long.valueOf(10L)).name("Bottes en cuir").layer(ClothingLayer.SHOES).warmthLevel(Integer.valueOf(3)).isWaterproof(Boolean.valueOf(true)).isWindproof(Boolean.valueOf(false))
        );
    }
}
