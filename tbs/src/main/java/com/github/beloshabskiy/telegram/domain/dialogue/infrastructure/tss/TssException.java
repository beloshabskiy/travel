package com.github.beloshabskiy.telegram.domain.dialogue.infrastructure.tss;

public class TssException extends RuntimeException {

    public TssException(String message) {
        super(message);
    }

    public TssException(Throwable t) {
        super(t);
    }
}

