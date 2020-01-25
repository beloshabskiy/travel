package com.github.beloshabskiy.ticketsearch.rest.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchResponse {

    private String currency;
    private List<Option> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private String flyFrom;
        private String flyTo;
        private String cityFrom;
        private String cityTo;
        private Integer price;
        private String departure;
        private String arrival;
        private List<Leg> route;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Leg {
        private String flyFrom;
        private String flyTo;
        private String cityFrom;
        private String cityTo;
        private String departure;
        private String arrival;
        private String airline;
    }
}
