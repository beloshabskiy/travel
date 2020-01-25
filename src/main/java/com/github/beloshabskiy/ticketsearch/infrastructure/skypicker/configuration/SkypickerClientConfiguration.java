package com.github.beloshabskiy.ticketsearch.infrastructure.skypicker.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.beloshabskiy.ticketsearch.infrastructure.skypicker.SkypickerHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkypickerClientConfiguration {

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        return HttpClients.createDefault();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public SkypickerHttpClient skypickerHttpClient(CloseableHttpClient httpClient,
                                                   ObjectMapper objectMapper,
                                                   @Value("${tss.skypicker.partner:picky}") String partner) {
        return new SkypickerHttpClient(httpClient, objectMapper, partner);
    }
}
