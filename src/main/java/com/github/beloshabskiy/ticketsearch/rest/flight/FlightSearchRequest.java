package com.github.beloshabskiy.ticketsearch.rest.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class FlightSearchRequest {
    private String from;
    private String to;
    private String dateFrom;
    private String dateTo;
    private String returnDateFrom;
    private String returnDateTo;
    private String currency;
    private Integer limit;
}
