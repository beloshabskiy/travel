package com.github.beloshabskiy.ticketsearch.rest.flight;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class FlightSearchRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return FlightSearchRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FlightSearchRequest request = (FlightSearchRequest) target;
    }
}
