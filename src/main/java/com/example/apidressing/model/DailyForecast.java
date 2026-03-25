package com.example.apidressing.model;

import lombok.Getter;

@Getter
public class DailyForecast {
    private final double averageTemperature;
    private final double maxTemperature;
    private final double minTemperature;
    private final double precipitationProbability;
    private final double windSpeed;
    private final String weatherCondition; // Ex: "Rain", "Clouds"

    public DailyForecast(double averageTemperature, double maxTemperature, double minTemperature, double precipitationProbability, double windSpeed, String weatherCondition) {
        this.averageTemperature = averageTemperature;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.precipitationProbability = precipitationProbability;
        this.windSpeed = windSpeed;
        this.weatherCondition = weatherCondition;
    }

}
