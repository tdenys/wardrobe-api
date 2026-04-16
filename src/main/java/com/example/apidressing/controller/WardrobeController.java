package com.example.apidressing.controller;

import com.example.apidressing.gen.api.WardrobeApi;
import com.example.apidressing.gen.model.ClothingItem;
import com.example.apidressing.gen.model.ClothingLayer;
import com.example.apidressing.gen.model.Outfit;
import com.example.apidressing.gen.model.WeatherSuggestion;
import com.example.apidressing.model.DailyForecast;
import com.example.apidressing.service.ClothingItemService;
import com.example.apidressing.service.OutfitRecommendationService;
import com.example.apidressing.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WardrobeController implements WardrobeApi {

    private final WeatherService weatherService;
    private final OutfitRecommendationService recommendationService;
    private final ClothingItemService clothingItemService;

    @Override
    public ResponseEntity<List<ClothingItem>> listClothingItems(ClothingLayer layer) {
        return new ResponseEntity<>(clothingItemService.listItems(layer), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ClothingItem> addClothingItem(String name, ClothingLayer layer, Integer warmthLevel, Boolean isWaterproof, Boolean isWindproof, MultipartFile image, String mainColorHex) {
        ClothingItem created = clothingItemService.addItem(image, name, layer, warmthLevel, isWaterproof, isWindproof, mainColorHex);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteClothingItem(Long id) {
        if (!clothingItemService.deleteItem(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ClothingItem> userWardrobe = clothingItemService.listItems(null);
        Outfit recommendedOutfit = recommendationService.recommendOutfit(forecast, userWardrobe);

        WeatherSuggestion suggestion = new WeatherSuggestion();
        suggestion.setTemperature(Float.valueOf((float) forecast.getAverageTemperature()));
        suggestion.setDescription(forecast.getWeatherCondition());
        suggestion.setSuggestedOutfit(recommendedOutfit);
        suggestion.setReasoning(buildReasoning(forecast));

        return new ResponseEntity<>(suggestion, HttpStatus.OK);
    }

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
}
