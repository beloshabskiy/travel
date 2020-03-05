package com.github.beloshabskiy.telegram.domain.dialogue.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.util.Set;

@Configuration
public class TelegramBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(Set<LongPollingBot> allBots) throws TelegramApiRequestException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        for (LongPollingBot bot : allBots) {
            telegramBotsApi.registerBot(bot);
        }
        return telegramBotsApi;
    }
}
