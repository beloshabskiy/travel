package com.github.beloshabskiy.ticketsearch.infrastructure.skypicker;

import lombok.Getter;
import org.apache.http.StatusLine;

@Getter
public class SkypickerException extends Exception {

    public SkypickerException(StatusLine statusLine) {
        super("Skypicker responses with " + statusLine);
    }
}
