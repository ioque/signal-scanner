package ru.ioque.investfund.adapters.exchagne.moex.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.history.HistoryValueDto;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.instrument.InstrumentDto;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.intraday.IntradayValueDto;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeRestClient {
    @Value("${exchange.url}")
    String exchangeUrl;
    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    final RestClient defaultClient = RestClient.create();

    @SneakyThrows
    public List<InstrumentDto> fetchInstruments() {
        return objectMapper
            .readValue(
                defaultClient
                    .get()
                    .uri(exchangeUrl + "/api/instruments")
                    .retrieve()
                    .body(String.class),
                new TypeReference<>() {}
            );
    }

    @SneakyThrows
    public List<IntradayValueDto> fetchIntradayValues(String ticker, int start) {
        return objectMapper
            .readValue(
                defaultClient
                    .get()
                    .uri(exchangeUrl + "/api/instruments/" + ticker + "/intraday?start=" + start)
                    .retrieve()
                    .body(String.class),
                new TypeReference<>() {}
            );
    }

    @SneakyThrows
    public List<HistoryValueDto> fetchHistory(
        String ticker,
        LocalDate from,
        LocalDate to
    ) {
        return objectMapper
            .readValue(
                defaultClient
                    .get()
                    .uri(exchangeUrl + "/api/instruments/" + ticker + "/history?from=" + from + "&to=" + to)
                    .retrieve()
                    .body(String.class),
                new TypeReference<>() {}
            );
    }
}
