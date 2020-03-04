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

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
public class SkypickerHttpClient implements Closeable {
    private final CloseableHttpClient httpClient;
    private final SkypickerRequestMapper mapper;
    private final ObjectMapper objectMapper;

    public SkypickerResponseDto sendRequest(SkypickerRequestDto request) throws SkypickerException {
        HttpGet get = new HttpGet(mapper.toUriString(request));
        try (CloseableHttpResponse response = httpClient.execute(get)) {
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                return objectMapper.readValue(response.getEntity().getContent(), SkypickerResponseDto.class);
            } else {
                if (response.getEntity() != null) {
                    String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                    log.error("Received {} response: {}", statusLine, responseBody);
                } else {
                    log.error("Received {} response with no body", statusLine);
                }
                throw new SkypickerException(statusLine);
            }
        } catch (IOException e) {
            throw new SkypickerException(e);
        }
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
