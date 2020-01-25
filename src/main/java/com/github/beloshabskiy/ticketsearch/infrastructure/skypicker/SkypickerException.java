package com.github.beloshabskiy.ticketsearch.infrastructure.skypicker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkypickerException extends Exception {
    private final int statusCode;
}
