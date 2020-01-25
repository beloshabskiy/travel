package com.github.beloshabskiy.ticketsearch.infrastructure.skypicker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SkypickerResponseDto {
    private String currency;
    private List<OptionDto> data;

    @Data
    private static class OptionDto {
        private String flyFrom;
        private String flyTo;
        private String cityFrom;
        private String cityTo;
        private Integer price;
        @JsonProperty("aTime")
        private Long aTime;
        @JsonProperty("dTime")
        private Long dTime;
        private List<LegDto> route;
    }

    @Data
    private static class LegDto {
        private String flyFrom;
        private String flyTo;
        private String cityFrom;
        private String cityTo;
        @JsonProperty("aTime")
        private Long aTime;
        @JsonProperty("dTime")
        private Long dTime;
        private String airline;
    }

}
