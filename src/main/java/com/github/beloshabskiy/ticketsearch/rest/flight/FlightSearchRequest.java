package com.github.beloshabskiy.ticketsearch.rest.flight;

import lombok.Data;

@Data
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
