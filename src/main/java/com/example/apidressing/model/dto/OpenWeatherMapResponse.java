package com.example.apidressing.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherMapResponse {
    @JsonProperty("daily")
    private List<DailyData> daily;

    public List<DailyData> getDaily() { return daily; }
    public void setDaily(List<DailyData> daily) { this.daily = daily; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyData {
        @JsonProperty("temp")
        private TempData temp;
        @JsonProperty("pop")
        private double pop; // Probability of precipitation
        @JsonProperty("wind_speed")
        private double windSpeed;
        @JsonProperty("weather")
        private List<WeatherData> weather;

        public TempData getTemp() { return temp; }
        public void setTemp(TempData temp) { this.temp = temp; }
        public double getPop() { return pop; }
        public void setPop(double pop) { this.pop = pop; }
        public double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
        public List<WeatherData> getWeather() { return weather; }
        public void setWeather(List<WeatherData> weather) { this.weather = weather; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TempData {
        @JsonProperty("day")
        private double day;
        @JsonProperty("min")
        private double min;
        @JsonProperty("max")
        private double max;

        public double getDay() { return day; }
        public void setDay(double day) { this.day = day; }
        public double getMin() { return min; }
        public void setMin(double min) { this.min = min; }
        public double getMax() { return max; }
        public void setMax(double max) { this.max = max; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherData {
        @JsonProperty("main")
        private String main; // Ex: "Rain", "Clouds", "Clear"

        public String getMain() { return main; }
        public void setMain(String main) { this.main = main; }
    }
}
