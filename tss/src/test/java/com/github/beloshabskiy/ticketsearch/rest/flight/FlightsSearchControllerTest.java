package com.github.beloshabskiy.ticketsearch.rest.flight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.beloshabskiy.ticketsearch.TicketSearchApplication;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.MimeTypeUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = TicketSearchApplication.class)
class FlightsSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloseableHttpClient httpClient;

    @Test
    void shouldHandle200Properly() throws Exception {
        final CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        given(mockResponse.getStatusLine()).willReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        final BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ClassPathResource("responseExample.json").getInputStream());
        Mockito.when(mockResponse.getEntity()).thenReturn(entity);
        Mockito.when(httpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);
        final FlightSearchRequest request = FlightSearchRequest.builder()
                .from("LED")
                .to("SJC")
                .dateFrom("17/04/1993")
                .dateTo("09/10/1993")
                .returnDateFrom("27/12/2014")
                .returnDateTo("13/01/2020")
                .currency("RUB")
                .limit(1)
                .build();
        final String requestAsJsonString = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/flights")
                        .content(requestAsJsonString)
                        .contentType(MimeTypeUtils.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("currency").value("RUB"))
                .andExpect(jsonPath("options").isArray())
                .andExpect(jsonPath("options.size()").value(1))
                .andExpect(jsonPath("options[0].flyFrom").value("LED"))
                .andExpect(jsonPath("options[0].flyTo").value("SJC"))
                .andExpect(jsonPath("options[0].cityFrom").value("Saint Petersburg"))
                .andExpect(jsonPath("options[0].cityTo").value("San Jose"))
                .andExpect(jsonPath("options[0].price").value(24985))
                .andExpect(jsonPath("options[0].departure").value("18/11/2020 00:05"))
                .andExpect(jsonPath("options[0].arrival").value("18/11/2020 22:52"))
                .andExpect(jsonPath("options[0].route").isArray())
                .andExpect(jsonPath("options[0].route.size()").value(3))
                .andExpect(jsonPath("options[0].route[0].flyFrom").value("LED"))
                .andExpect(jsonPath("options[0].route[0].flyTo").value("SVO"))
                .andExpect(jsonPath("options[0].route[0].cityFrom").value("Saint Petersburg"))
                .andExpect(jsonPath("options[0].route[0].cityTo").value("Moscow"))
                .andExpect(jsonPath("options[0].route[0].departure").value("18/11/2020 00:05"))
                .andExpect(jsonPath("options[0].route[0].arrival").value("18/11/2020 01:30"))
                .andExpect(jsonPath("options[0].route[0].airline").value("SU"))
                .andExpect(jsonPath("options[0].route[1].flyFrom").value("SVO"))
                .andExpect(jsonPath("options[0].route[1].flyTo").value("LAX"))
                .andExpect(jsonPath("options[0].route[1].cityFrom").value("Moscow"))
                .andExpect(jsonPath("options[0].route[1].cityTo").value("Los Angeles"))
                .andExpect(jsonPath("options[0].route[1].departure").value("18/11/2020 11:35"))
                .andExpect(jsonPath("options[0].route[1].arrival").value("18/11/2020 12:45"))
                .andExpect(jsonPath("options[0].route[1].airline").value("SU"))
                .andExpect(jsonPath("options[0].route[2].flyFrom").value("LAX"))
                .andExpect(jsonPath("options[0].route[2].flyTo").value("SJC"))
                .andExpect(jsonPath("options[0].route[2].cityFrom").value("Los Angeles"))
                .andExpect(jsonPath("options[0].route[2].cityTo").value("San Jose"))
                .andExpect(jsonPath("options[0].route[2].departure").value("18/11/2020 21:35"))
                .andExpect(jsonPath("options[0].route[2].arrival").value("18/11/2020 22:52"))
                .andExpect(jsonPath("options[0].route[2].airline").value("AA"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void shouldHandle400Properly() throws Exception {
        final FlightSearchRequest requestWithoutFromAndTo = FlightSearchRequest.builder()
                .dateFrom("17/04/1993")
                .dateTo("09/10/1993")
                .returnDateFrom("27/12/2014")
                .returnDateTo("13/01/2020")
                .currency("RUB")
                .limit(1)
                .build();
        final String requestAsJsonString = new ObjectMapper().writeValueAsString(requestWithoutFromAndTo);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/flights")
                        .content(requestAsJsonString)
                        .contentType(MimeTypeUtils.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"'from' and 'to' can't be both null\"}"))
                .andDo(MockMvcResultHandlers.print());
        verifyNoInteractions(httpClient);
    }

}