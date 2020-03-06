package com.github.beloshabskiy.telegram.domain.dialogue.infrastructure.tss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.beloshabskiy.ticketsearch.rest.flight.FlightSearchRequest;
import com.github.beloshabskiy.ticketsearch.rest.flight.FlightSearchResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;

@Component
public class TssClient {
    private final String tssUrl;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TssClient(@Value("${tss.url}") String tssUrl,
                     CloseableHttpClient httpClient,
                     ObjectMapper objectMapper) {
        this.tssUrl = tssUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public FlightSearchResponse findTickets(FlightSearchRequest request) {
        try {
            HttpPost post = new HttpPost(tssUrl + "/flights");
            final String serializedRequest = objectMapper.writeValueAsString(request);
            post.setEntity(new StringEntity(serializedRequest, StandardCharsets.UTF_8));
            post.addHeader(HttpHeaders.ACCEPT, MimeTypeUtils.APPLICATION_JSON_VALUE);
            post.addHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);

            return httpClient.execute(post, response -> {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new TssException("Unexpected status code: " + response.getStatusLine().getStatusCode());
                }
                return objectMapper.readValue(response.getEntity().getContent(), FlightSearchResponse.class);
            });

        } catch (TssException e) {
            throw e;
        } catch (Exception e) {
            throw new TssException(e);
        }
    }
}
