package com.github.beloshabskiy.ticketsearch.rest.flight;

import com.github.beloshabskiy.ticketsearch.infrastructure.skypicker.SkypickerException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FlightsSearchController {
    private final FlightSearchRequestHandler handler;
    private final FlightSearchRequestValidator validator;

    @PostMapping("/flights")
    public FlightSearchResponse findTickets(@RequestBody FlightSearchRequest request) throws SkypickerException {
        validator.validate(request);
        return handler.handle(request);
    }
}
