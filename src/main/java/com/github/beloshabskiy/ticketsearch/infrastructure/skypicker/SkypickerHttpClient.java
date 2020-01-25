package com.github.beloshabskiy.ticketsearch.infrastructure.skypicker;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
public class SkypickerHttpClient implements Closeable {
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final String partner;

    public SkypickerResponseDto sendRequest(SkypickerRequestDto request) throws IOException, SkypickerException {
        HttpGet get = new HttpGet(buildRequestUrl(request));
        try (CloseableHttpResponse response = httpClient.execute(get)) {
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                final String s = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                return objectMapper.readValue(s, SkypickerResponseDto.class);
            } else {
                if (response.getEntity() != null) {
                    String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                    log.error("Received {} response: {}", statusLine, responseBody);
                } else {
                    log.error("Received {} response with no body", statusLine);
                }
                throw new SkypickerException(statusLine);
            }

        }
    }

    private String buildRequestUrl(SkypickerRequestDto request) {
        return IgnoreNullsUriComponentsBuilder.from(UriComponentsBuilder.fromHttpUrl("https://api.skypicker.com/flights"))
                .queryParam("fly_from", request.getFlyFrom())
                .queryParam("fly_to", request.getFlyTo())
                .queryParam("date_from", request.getDateFrom())
                .queryParam("date_to", request.getDateTo())
                .queryParam("return_from", request.getReturnFrom())
                .queryParam("return_to", request.getReturnTo())
                .queryParam("curr", request.getCurrency())
                .queryParam("limit", request.getLimit())
                .queryParam("max_stopovers", request.getMaxStopovers())
                .queryParam("partner", partner)
                .build()
                .toUriString();
    }

    @AllArgsConstructor(staticName = "from")
    private static class IgnoreNullsUriComponentsBuilder {
        private final UriComponentsBuilder delegate;

        public IgnoreNullsUriComponentsBuilder queryParam(String name, Object value) {
            if (value != null) {
                delegate.queryParam(name, value);
            }
            return this;
        }

        public UriComponents build() {
            return delegate.build();
        }
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
