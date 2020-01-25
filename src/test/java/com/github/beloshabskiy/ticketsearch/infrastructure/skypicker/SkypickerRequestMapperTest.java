package com.github.beloshabskiy.ticketsearch.infrastructure.skypicker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SkypickerRequestMapperTest {
    private final SkypickerRequestMapper underTest = new SkypickerRequestMapper("somePartner");

    @Test
    void shouldConvertAllFieldToQueryParams() {
        final SkypickerRequestDto request = SkypickerRequestDto.builder()
                .flyFrom("LED")
                .flyTo("IAD")
                .dateFrom("17/04/1993")
                .dateTo("09/10/1993")
                .returnFrom("27/12/2014")
                .returnTo("13/01/2020")
                .currency("RUB")
                .limit(1)
                .maxStopovers(0)
                .build();

        Assertions.assertEquals(
                "https://api.skypicker.com/flights?fly_from=LED&fly_to=IAD&date_from=17/04/1993&date_to=09/10/1993&return_from=27/12/2014&return_to=13/01/2020&curr=RUB&limit=1&max_stopovers=0&v=3&partner=somePartner",
                underTest.toUriString(request)
        );
    }

    @Test
    void shouldNotIncludeEmptyFields() {
        final SkypickerRequestDto request = SkypickerRequestDto.builder()
                .flyFrom("LED")
                .flyTo("IAD")
                .dateFrom("17/04/1993")
                .dateTo("09/10/1993")
                .returnFrom("27/12/2014")
                .returnTo("13/01/2020")
                .currency(null)
                .limit(1)
                .maxStopovers(0)
                .build();

        Assertions.assertEquals(
                "https://api.skypicker.com/flights?fly_from=LED&fly_to=IAD&date_from=17/04/1993&date_to=09/10/1993&return_from=27/12/2014&return_to=13/01/2020&limit=1&max_stopovers=0&v=3&partner=somePartner",
                underTest.toUriString(request)
        );
    }
}