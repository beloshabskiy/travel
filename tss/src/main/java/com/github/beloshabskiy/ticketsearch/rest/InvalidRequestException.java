package com.github.beloshabskiy.ticketsearch.rest;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
