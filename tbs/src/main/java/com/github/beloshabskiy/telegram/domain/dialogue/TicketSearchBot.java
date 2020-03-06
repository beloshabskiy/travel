package com.github.beloshabskiy.telegram.domain.dialogue;

import com.github.beloshabskiy.telegram.domain.dialogue.infrastructure.tss.TssClient;
import com.github.beloshabskiy.ticketsearch.rest.flight.FlightSearchRequest;
import com.github.beloshabskiy.ticketsearch.rest.flight.FlightSearchResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Component
public class TicketSearchBot extends TelegramLongPollingSessionBot {
    private static final String DIALOGUE_SESSION_KEY = "dialogue";
    private final String botUsername;
    private final String botToken;
    private final TssClient tssClient;

    public TicketSearchBot(@Value("${bot.username}") String botUsername,
                           @Value("${bot.token}") String botToken,
                           TssClient tssClient) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.tssClient = tssClient;
    }

    @Override
    public void onUpdateReceived(Update update, Optional<Session> botSession) {
        MDC.clear();
        Optional.of(update)
                .map(Update::getMessage)
                .map(Message::getFrom)
                .map(User::getId)
                .map(Object::toString)
                .ifPresent(id -> MDC.put("user_id", id));
        try {
            botSession.ifPresent(session -> processUpdate(update, session));
        } catch (Exception e) {
            log.error("Can't process update {}", update, e);
        } finally {
            MDC.clear();
        }
    }

    private void processUpdate(Update update, Session session) {
        TicketSearchDialogue dialogue = (TicketSearchDialogue) session.getAttribute(DIALOGUE_SESSION_KEY);
        final String userInput = update.getMessage().getText();
        if (dialogue == null || "/start".equals(userInput)) {
            dialogue = new TicketSearchDialogue();
            session.setAttribute("dialogue", dialogue);
        }
        log.info("Incoming message {}, dialogue status: {}",
                update.getMessage().getText(),
                dialogue.toString()
        );
        if (dialogue.isNotStarted()) {
            sendMessage(update.getMessage(), dialogue.greeting());
            sendMessage(update.getMessage(), dialogue.initiate());
        } else if (!dialogue.isFinished()) {
            final TicketSearchDialogue.Answer answer = dialogue.answer(userInput);
            sendMessage(update.getMessage(), answer.getMessage(), answer.getOptions());
        }
        if (dialogue.isFinished()) {
            final FlightSearchRequest request = dialogue.buildRequest();
            final FlightSearchResponse response = tssClient.findTickets(request);
            sendMessage(update.getMessage(), format(response));
            session.setAttribute("dialogue", null);
        }
        log.info("Message from {} processed, dialogue status: {}",
                update.getMessage().getFrom().getUserName(),
                dialogue.toString()
        );
    }

    private String format(FlightSearchResponse response) { // TODO extract
        if (response.getOptions().isEmpty()) {
            return "По вашему запросу ничего не найдено";
        } else {
            final FlightSearchResponse.Option option = response.getOptions().get(0);
            return MessageFormat.format(
                    "Самый дешёвый билет:\n{0} {1}\n{2} {3}\n{4}->{5}\n{6} {7}",
                    response.getCurrency(),
                    option.getPrice(),
                    option.getCityFrom(),
                    option.getRoute().get(0).getDeparture(),
                    option.getFlyFrom(),
                    option.getRoute().stream().map(FlightSearchResponse.Leg::getFlyTo).collect(Collectors.joining("->")),
                    option.getCityTo(),
                    option.getArrival()
            );
        }
    }

    private void sendMessage(Message message, String text) {
        sendMessage(message, text, Collections.emptyList());
    }

    private void sendMessage(Message message, String text, List<String> buttons) {
        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setChatId(message.getChatId())
                .setText(text);

        if (!buttons.isEmpty()) {
            sendMessage.setReplyMarkup(buildButtons(buttons));
        } else {
            sendMessage.setReplyMarkup(null);
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Can't send message", e);
        }
    }

    private ReplyKeyboardMarkup buildButtons(List<String> options) {
        ReplyKeyboardMarkup result = new ReplyKeyboardMarkup();
        result.setSelective(true);
        result.setResizeKeyboard(true);
        result.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        for (String option : options) {
            keyboardFirstRow.add(new KeyboardButton(option));
        }

        keyboardRowList.add(keyboardFirstRow);
        result.setKeyboard(keyboardRowList);
        return result;
    }
}
