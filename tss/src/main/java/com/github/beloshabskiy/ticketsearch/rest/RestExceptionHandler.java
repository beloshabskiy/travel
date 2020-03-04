package com.github.beloshabskiy.ticketsearch.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(InvalidRequestException e) {
        return ResponseEntity.badRequest().body(ErrorResponse.from(e.getMessage()));
    }

    @Data
    @AllArgsConstructor(staticName = "from")
    private static class ErrorResponse {
        private final String error;
    }
}
