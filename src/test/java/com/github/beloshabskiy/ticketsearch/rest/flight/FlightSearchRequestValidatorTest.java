package com.github.beloshabskiy.ticketsearch.rest.flight;

import com.github.beloshabskiy.ticketsearch.rest.InvalidRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FlightSearchRequestValidatorTest {
    private final FlightSearchRequestValidator underTest = new FlightSearchRequestValidator();

    @Test
    void shouldAcceptValidRequest() {
        underTest.validate(validRoundTripRequest());
        underTest.validate(validOneWayRequest());
        underTest.validate(validToAnywhereRequest());
        underTest.validate(validToAnywhereAtAnytimeRequest());
    }

    @Test
    void shouldRejectWithoutBothFromAndTo() {
        final FlightSearchRequest invalidRequest = validOneWayRequest().setFrom(null).setTo(null);
        Assertions.assertThrows(InvalidRequestException.class, () -> underTest.validate(invalidRequest));
    }

    @Test
    void shouldRejectWithInvalidDateFormat() {
        final String invalidDate = "09.10.1993";
        Assertions.assertThrows(InvalidRequestException.class, () -> underTest.validate(validRoundTripRequest().setDateFrom(invalidDate)));
        Assertions.assertThrows(InvalidRequestException.class, () -> underTest.validate(validRoundTripRequest().setDateTo(invalidDate)));
        Assertions.assertThrows(InvalidRequestException.class, () -> underTest.validate(validRoundTripRequest().setReturnDateFrom(invalidDate)));
        Assertions.assertThrows(InvalidRequestException.class, () -> underTest.validate(validRoundTripRequest().setReturnDateTo(invalidDate)));
    }

    private FlightSearchRequest validRoundTripRequest() {
        return FlightSearchRequest.builder()
                .from("LED")
                .to("IAD")
                .dateFrom("17/04/1993")
                .dateTo("09/10/1993")
                .returnDateFrom("27/12/2014")
                .returnDateTo("13/01/2020")
                .currency("RUB")
                .limit(1)
                .build();
    }

    private FlightSearchRequest validOneWayRequest() {
        return FlightSearchRequest.builder()
                .from("LED")
                .to("IAD")
                .dateFrom("17/04/1993")
                .dateTo("09/10/1993")
                .build();
    }

    private FlightSearchRequest validToAnywhereRequest() {
        return FlightSearchRequest.builder()
                .from("LED")
                .dateFrom("17/04/1993")
                .dateTo("09/10/1993")
                .build();
    }

    private FlightSearchRequest validToAnywhereAtAnytimeRequest() {
        return FlightSearchRequest.builder()
                .from("LED")
                .dateFrom("17/04/1993")
                .dateTo("09/10/1993")
                .build();
    }

}