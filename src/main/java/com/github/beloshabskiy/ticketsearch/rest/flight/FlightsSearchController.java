package com.github.beloshabskiy.ticketsearch.rest.flight;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FlightsSearchController {
    private final FlightSearchRequestHandler handler;

    @PostMapping("/flights")
    public FlightSearchResponse findTickets(@Validated @RequestBody FlightSearchRequest request) throws Exception {
        return handler.handle(request);
    }
}
