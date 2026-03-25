package com.example.apidressing.service;

import com.example.apidressing.model.DailyForecast;
import com.example.apidressing.model.dto.GeocodingResponse;
import com.example.apidressing.model.dto.OpenWeatherMapResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public WeatherService(RestTemplate restTemplate, @Value("${weather.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    /**
     * Obtient les prévisions pour une ville donnée.
     * Utilise l'API de Geocoding pour trouver les coordonnées, puis appelle les prévisions.
     */
    public DailyForecast getDailyForecast(String city) {
        GeocodingResponse[] geocodingResponses = getCoordinatesForCity(city);

        if (geocodingResponses != null && geocodingResponses.length > 0) {
            GeocodingResponse location = geocodingResponses[0];
            return getDailyForecast(location.getLat(), location.getLon());
        }

        return null; // Ou lancer une exception "City not found"
    }

    /**
     * Obtient les prévisions pour des coordonnées géographiques données.
     */
    public DailyForecast getDailyForecast(double lat, double lon) {
        // On utilise l'API "One Call" qui est plus complète
        String apiUrl = String.format(
            "https://api.openweathermap.org/data/3.0/onecall?lat=%f&lon=%f&exclude=current,minutely,hourly,alerts&appid=%s&units=metric",
            lat, lon, apiKey
        );

        OpenWeatherMapResponse response = restTemplate.getForObject(apiUrl, OpenWeatherMapResponse.class);

        if (response != null && response.getDaily() != null && !response.getDaily().isEmpty()) {
            // On prend le premier jour de la prévision (aujourd'hui)
            OpenWeatherMapResponse.DailyData today = response.getDaily().getFirst();
            return new DailyForecast(
                today.getTemp().getDay(),
                today.getTemp().getMax(),
                today.getTemp().getMin(),
                today.getPop() * 100, // Probabilité en pourcentage
                today.getWindSpeed(),
                !today.getWeather().isEmpty() ? today.getWeather().getFirst().getMain() : "Unknown"
            );
        }

        return null; // Ou lancer une exception
    }

    /**
     * Appelle l'API de Geocoding d'OpenWeatherMap pour convertir une ville en coordonnées.
     */
    private GeocodingResponse[] getCoordinatesForCity(String city) {
        String apiUrl = String.format(
            "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s",
            city, apiKey
        );
        return restTemplate.getForObject(apiUrl, GeocodingResponse[].class);
    }
}
