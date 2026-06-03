package net.engineeringdigest.journalapp.api_response;

import lombok.Data;

@Data
public class WeatherResponse {

    private Location location;

    private Current current;

    @Data
    public static class Location {

        private String name;

        private String country;
    }

    @Data
    public static class Current {

        private double temp_c;

        private int humidity;

        private Condition condition;
    }

    @Data
    public static class Condition {

        private String text;
    }
}
