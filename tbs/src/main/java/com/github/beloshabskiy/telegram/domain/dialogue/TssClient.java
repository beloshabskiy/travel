package com.github.beloshabskiy.telegram.domain.dialogue;

import com.github.beloshabskiy.ticketsearch.rest.flight.FlightSearchRequest;
import com.github.beloshabskiy.ticketsearch.rest.flight.FlightSearchResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class TssClient {
    public FlightSearchResponse findTickets(FlightSearchRequest request) {
        return new FlightSearchResponse(
                "RUB",
                Collections.singletonList(
                        new FlightSearchResponse.Option(
                                "LED",
                                "JFK",
                                "Saint-Petersburg",
                                "New York",
                                300,
                                "18/11/2020 00:05",
                                "18/11/2020 22:52",
                                Collections.singletonList(
                                        new FlightSearchResponse.Leg(
                                                "LED",
                                                "JFK",
                                                "Saint-Petersburg",
                                                "New York",
                                                "18/11/2020 00:05",
                                                "18/11/2020 22:52",
                                                "DP"
                                        )
                                )
                        )
                )
        );
    }
}
