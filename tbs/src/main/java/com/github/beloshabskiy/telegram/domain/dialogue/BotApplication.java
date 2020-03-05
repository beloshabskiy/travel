package com.github.beloshabskiy.telegram.domain.dialogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.ApiContextInitializer;

@PropertySource("classpath:configuration.properties")
@SpringBootApplication
public class BotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(BotApplication.class);
    }
}
