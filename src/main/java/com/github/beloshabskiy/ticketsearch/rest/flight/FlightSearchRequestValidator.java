package com.github.beloshabskiy.ticketsearch.rest.flight;

import com.github.beloshabskiy.ticketsearch.rest.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;

@Component
public class FlightSearchRequestValidator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void validate(FlightSearchRequest request) {
        if (request.getFrom() == null && request.getTo() == null) {
            throw new InvalidRequestException("'from' and 'to' can't be both null");
        }

        if (dateFormatIsInvalid(request.getDateFrom())) {
            throw new InvalidRequestException("'dateFrom' is invalid; expected 'dd/MM/yyyy', got " + request.getDateFrom());
        }

        if (dateFormatIsInvalid(request.getDateTo())) {
            throw new InvalidRequestException("'dateFrom' is invalid; expected 'dd/MM/yyyy', got " + request.getDateTo());
        }

        if (dateFormatIsInvalid(request.getReturnDateFrom())) {
            throw new InvalidRequestException("'returnDateFrom' is invalid; expected 'dd/MM/yyyy', got " + request.getReturnDateFrom());
        }

        if (dateFormatIsInvalid(request.getReturnDateTo())) {
            throw new InvalidRequestException("'returnDateTo' is invalid; expected 'dd/MM/yyyy', got " + request.getReturnDateTo());
        }
    }

    private static boolean dateFormatIsInvalid(String date) {
        if (date == null) {
            return false;
        }
        try {
            FORMATTER.parse(date);
            return false;
        } catch (DateTimeException e) {
            return true;
        }
    }
}
