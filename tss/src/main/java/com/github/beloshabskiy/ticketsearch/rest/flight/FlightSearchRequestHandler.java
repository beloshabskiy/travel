package com.github.beloshabskiy.ticketsearch.rest.flight;

import com.github.beloshabskiy.ticketsearch.infrastructure.skypicker.SkypickerHttpClient;
import com.github.beloshabskiy.ticketsearch.infrastructure.skypicker.SkypickerRequestDto;
import com.github.beloshabskiy.ticketsearch.infrastructure.skypicker.SkypickerResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FlightSearchRequestHandler {
    private final SkypickerHttpClient skypickerHttpClient;
    private final FlightRequestMapper mapper;

    public FlightSearchResponse handle(FlightSearchRequest request) throws Exception {
        final SkypickerRequestDto requestDto = mapper.toSkypickerRequest(request);
        final SkypickerResponseDto responseDto = skypickerHttpClient.sendRequest(requestDto);
        return mapper.toDomainResponse(responseDto);
    }
}
