package com.github.beloshabskiy.telegram.domain.dialogue;

import com.github.beloshabskiy.ticketsearch.rest.flight.FlightSearchRequest;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class TicketSearchDialogue {
    private FlightSearchRequest.FlightSearchRequestBuilder builder;
    private Step step;

    TicketSearchDialogue() {
        step = Step.INITIAL;
    }

    String greeting() {
        return "Привет! Я умею искать билеты. Введи '/start' в любой момент, чтобы начать диалог сначала";
    }

    synchronized String initiate() {
        step = Step.FROM;
        builder = FlightSearchRequest.builder();
        return step.question;
    }

    synchronized boolean isNotStarted() {
        return step == Step.INITIAL;
    }

    synchronized boolean isFinished() {
        return step == Step.FINISHED;
    }

    synchronized FlightSearchRequest buildRequest() {
        if (isFinished()) {
            return builder.build();
        } else {
            throw new IllegalStateException("Dialogue is not finished yet");
        }
    }

    synchronized Answer answer(String userMessage) {
        if (step == null) {
            throw new IllegalStateException("Dialogue should be initiated via initiate() method!");
        }
        if (!step.isValidAnswer(userMessage)) {
            return new Answer(
                    "Некорректный ответ, попробуйте ещё раз",
                    step.options
            );
        }
        switch (step) {
            case FROM:
                builder.from(userMessage);
                step = Step.TO_OPTIONS;
                break;
            case TO_OPTIONS:
                if (step.options.get(0).equals(userMessage)) {
                    builder.to(userMessage);
                    step = Step.TO;
                } else if (step.options.get(1).equals(userMessage)) {
                    builder.to(null);
                    step = Step.DATE_OPTIONS;
                } else {
                    throw new IllegalStateException("Expected one of " + step.options + ", got " + userMessage);
                }
                break;
            case TO:
                builder.to(userMessage);
                step = Step.DATE_OPTIONS;
                break;
            case DATE_OPTIONS:
                if (step.options.get(0).equals(userMessage)) {
                    step = Step.EXACT_DATE;
                } else if (step.options.get(1).equals(userMessage)) {
                    step = Step.DATE_FROM;
                } else {
                    throw new IllegalStateException("Expected one of " + step.options + ", got " + userMessage);
                }
                break;
            case EXACT_DATE:
                builder.dateFrom(userMessage);
                builder.dateTo(userMessage);
                step = Step.ONE_WAY_OR_ROUND_TRIP;
                break;
            case DATE_FROM:
                builder.dateFrom(userMessage);
                step = Step.DATE_TO;
                break;
            case DATE_TO:
                builder.dateTo(userMessage);
                step = Step.ONE_WAY_OR_ROUND_TRIP;
                break;
            case ONE_WAY_OR_ROUND_TRIP:
                if (step.options.get(0).equals(userMessage)) {
                    step = Step.RETURN_DATE_OPTIONS;
                } else if (step.options.get(1).equals(userMessage)) {
                    step = Step.FINISHED;
                } else {
                    throw new IllegalStateException("Expected one of " + step.options + ", got " + userMessage);
                }
                break;
            case RETURN_DATE_OPTIONS:
                if (step.options.get(0).equals(userMessage)) {
                    step = Step.EXACT_RETURN_DATE;
                } else if (step.options.get(1).equals(userMessage)) {
                    step = Step.RETURN_DATE_FROM;
                } else {
                    throw new IllegalStateException("Expected one of " + step.options + ", got " + userMessage);
                }
                break;
            case EXACT_RETURN_DATE:
                builder.returnDateFrom(userMessage);
                builder.returnDateTo(userMessage);
                step = Step.FINISHED;
                break;
            case RETURN_DATE_FROM:
                builder.returnDateFrom(userMessage);
                step = Step.RETURN_DATE_TO;
                break;
            case RETURN_DATE_TO:
                builder.returnDateTo(userMessage);
                step = Step.FINISHED;
                break;
        }
        return new Answer(
                step.question,
                step.options
        );
    }

    private enum Step {
        INITIAL,
        FROM("IATA-код аэропорта вылета?") {
            @Override
            boolean isValidAnswer(String answer) {
                return answer != null && answer.matches("[A-Z]{3}");
            }
        },
        TO_OPTIONS("Куда?", Arrays.asList("Код аэропорта", "Куда угодно")) {
            @Override
            boolean isValidAnswer(String answer) {
                return options.contains(answer);
            }
        },
        TO("IATA-код аэропорта назначения?") {
            @Override
            boolean isValidAnswer(String answer) {
                return answer != null && answer.matches("[A-Z]{3}");
            }
        },
        DATE_OPTIONS("Когда?", Arrays.asList("Точная дата", "Диапазон дат")),
        EXACT_DATE("Дата в формате dd/MM/yyyy?") {
            @Override
            boolean isValidAnswer(String answer) {
                return isValidDate(answer);
            }
        },
        DATE_FROM("Начало диапазона в формате dd/MM/yyyy?") {
            @Override
            boolean isValidAnswer(String answer) {
                return isValidDate(answer);
            }
        },
        DATE_TO("Конец диапазона в формате dd/MM/yyyy?") {
            @Override
            boolean isValidAnswer(String answer) {
                return isValidDate(answer);
            }
        },
        ONE_WAY_OR_ROUND_TRIP("Обратный билет?", Arrays.asList("Да", "Нет")),
        RETURN_DATE_OPTIONS("Когда обратно?", Arrays.asList("Точная дата", "Диапазон дат")),
        EXACT_RETURN_DATE("Дата в формате dd/MM/yyyy?") {
            @Override
            boolean isValidAnswer(String answer) {
                return isValidDate(answer);
            }
        },
        RETURN_DATE_FROM("Начало диапазона в формате dd/MM/yyyy?") {
            @Override
            boolean isValidAnswer(String answer) {
                return isValidDate(answer);
            }
        },
        RETURN_DATE_TO("Конец диапазона в формате dd/MM/yyyy?") {
            @Override
            boolean isValidAnswer(String answer) {
                return isValidDate(answer);
            }
        },
        FINISHED("Ищу билеты");

        final String question;
        final List<String> options;

        Step() {
            this.question = null;
            this.options = Collections.emptyList();
        }

        Step(String question) {
            this.question = question;
            this.options = Collections.emptyList();
        }

        Step(String question, List<String> options) {
            this.question = question;
            this.options = options;
        }

        boolean isValidAnswer(String answer) {
            return options.contains(answer);
        }
    }

    private static boolean isValidDate(String input) {
        if (input == null) {
            return false;
        }
        try {
            LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Value
    public static class Answer {
        private final String message;
        private final List<String> options;
    }

    @Override
    public String toString() {
        return step.toString();
    }
}
