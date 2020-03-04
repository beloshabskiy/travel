package com.github.beloshabskiy.ticketsearch.infrastructure.skypicker;

import lombok.AllArgsConstructor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor
public class SkypickerRequestMapper {
    private static final int API_VERSION = 3;
    private final String partner;

    public String toUriString(SkypickerRequestDto request) {
        return IgnoreNullsUriComponentsBuilder.from(UriComponentsBuilder.fromHttpUrl("https://api.skypicker.com/flights"))
                .queryParam("fly_from", request.getFlyFrom())
                .queryParam("fly_to", request.getFlyTo())
                .queryParam("date_from", request.getDateFrom())
                .queryParam("date_to", request.getDateTo())
                .queryParam("return_from", request.getReturnFrom())
                .queryParam("return_to", request.getReturnTo())
                .queryParam("curr", request.getCurrency())
                .queryParam("limit", request.getLimit())
                .queryParam("max_stopovers", request.getMaxStopovers())
                .queryParam("v", API_VERSION)
                .queryParam("partner", partner)
                .build()
                .toUriString();
    }

    @AllArgsConstructor(staticName = "from")
    private static class IgnoreNullsUriComponentsBuilder {
        private final UriComponentsBuilder delegate;

        public IgnoreNullsUriComponentsBuilder queryParam(String name, Object value) {
            if (value != null) {
                delegate.queryParam(name, value);
            }
            return this;
        }

        public UriComponents build() {
            return delegate.build();
        }
    }
}
