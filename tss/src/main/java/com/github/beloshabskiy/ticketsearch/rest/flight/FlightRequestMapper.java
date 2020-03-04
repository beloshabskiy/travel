package com.github.beloshabskiy.ticketsearch.rest.flight;

import com.github.beloshabskiy.ticketsearch.infrastructure.skypicker.SkypickerRequestDto;
import com.github.beloshabskiy.ticketsearch.infrastructure.skypicker.SkypickerResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightRequestMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    SkypickerRequestDto toSkypickerRequest(FlightSearchRequest request) {
        return SkypickerRequestDto.builder()
                .flyFrom(request.getFrom())
                .flyTo(request.getTo())
                .dateFrom(request.getDateFrom())
                .dateTo(request.getDateTo())
                .returnFrom(request.getReturnDateFrom())
                .returnTo(request.getReturnDateTo())
                .currency(request.getCurrency())
                .limit(request.getLimit())
                .build();
    }

    FlightSearchResponse toDomainResponse(SkypickerResponseDto response) {
        return FlightSearchResponse.builder()
                .currency(response.getCurrency())
                .options(toDomainOptions(response.getData()))
                .build();
    }

    private List<FlightSearchResponse.Option> toDomainOptions(Collection<SkypickerResponseDto.OptionDto> options) {
        return options == null ? Collections.emptyList()
                               : options.stream().map(this::toDomainOption).collect(Collectors.toUnmodifiableList());
    }

    private FlightSearchResponse.Option toDomainOption(SkypickerResponseDto.OptionDto option) {
        return FlightSearchResponse.Option.builder()
                .flyFrom(option.getFlyFrom())
                .flyTo(option.getFlyTo())
                .cityFrom(option.getCityFrom())
                .cityTo(option.getCityTo())
                .price(option.getPrice())
                .departure(toLocalDate(option.getDTime()))
                .arrival(toLocalDate(option.getATime()))
                .route(toDomainRoute(option.getRoute()))
                .build();
    }

    private List<FlightSearchResponse.Leg> toDomainRoute(Collection<SkypickerResponseDto.LegDto> route) {
        return route == null ? Collections.emptyList()
                             : route.stream().map(this::toDomainLeg).collect(Collectors.toUnmodifiableList());
    }

    private FlightSearchResponse.Leg toDomainLeg(SkypickerResponseDto.LegDto leg) {
        return FlightSearchResponse.Leg.builder()
                .flyFrom(leg.getFlyFrom())
                .flyTo(leg.getFlyTo())
                .cityFrom(leg.getCityFrom())
                .cityTo(leg.getCityTo())
                .departure(toLocalDate(leg.getDTime()))
                .arrival(toLocalDate(leg.getATime()))
                .airline(leg.getAirline())
                .build();
    }

    private static String toLocalDate(Long localEpochSeconds) {
        if (localEpochSeconds == null) {
            return null;
        } else {
            return LocalDateTime.ofEpochSecond(localEpochSeconds, 0, ZoneOffset.UTC).format(FORMATTER);
        }
    }
}
