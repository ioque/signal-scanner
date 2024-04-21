package ru.ioque.investfund.adapters.datasource.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.ioque.investfund.application.datasource.integration.dto.history.AggregateHistoryDto;
import ru.ioque.investfund.application.datasource.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.datasource.integration.dto.intraday.IntradayValueDto;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatasourceRestClient {
    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    final RestClient defaultClient = RestClient.create();

    public List<InstrumentDto> fetchInstruments(String exchangeUrl) {
        try {
            return objectMapper
                .readValue(
                    defaultClient
                        .get()
                        .uri(exchangeUrl + "/api/instruments")
                        .retrieve()
                        .body(String.class),
                    new TypeReference<>() {}
                );
        } catch (Exception ex) {
            throw new ExchangeRestClientException(ex.getMessage(), ex);
        }
    }

    public List<IntradayValueDto> fetchIntradayValues(String exchangeUrl, String ticker, long lastNumber) {
        try {
            return objectMapper
                .readValue(
                    defaultClient
                        .get()
                        .uri(exchangeUrl + "/api/instruments/" + ticker + "/intraday?lastNumber=" + lastNumber)
                        .retrieve()
                        .body(String.class),
                    new TypeReference<>() {}
                );
        } catch (Exception ex) {
            throw new ExchangeRestClientException(ex.getMessage(), ex);
        }
    }

    public List<AggregateHistoryDto> fetchHistory(
        String exchangeUrl,
        String ticker,
        LocalDate from,
        LocalDate to
    ) {
        try {
            if (from.isEqual(to)) return List.of();
            return objectMapper
                .readValue(
                    defaultClient
                        .get()
                        .uri(exchangeUrl + "/api/instruments/" + ticker + "/history?from=" + from + "&to=" + to)
                        .retrieve()
                        .body(String.class),
                    new TypeReference<>() {}
                );
        } catch (Exception ex) {
            throw new ExchangeRestClientException(ex.getMessage(), ex);
        }
    }
}

