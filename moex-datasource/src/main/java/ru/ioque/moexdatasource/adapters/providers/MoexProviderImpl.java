package ru.ioque.moexdatasource.adapters.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.ioque.moexdatasource.application.adapters.MoexProvider;
import ru.ioque.moexdatasource.domain.instrument.Instrument;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MoexProviderImpl implements MoexProvider {
    QueryBuilder queryBuilder;
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    RestClient defaultClient = RestClient.create();

    @SneakyThrows
    public List<JsonNode> fetchInstruments(Class<? extends Instrument> type) {
        return List.of(
            objectMapper
                .readTree(fetch(queryBuilder.getSecurityListQuery(type)))
                .get("securities")
        );
    }

    @Override
    public List<JsonNode> fetchHistory(
        Instrument instrument,
        LocalDate from,
        LocalDate to
    ) {
        return PageReader
            .builder()
            .start(0)
            .pageSize(100)
            .url(dailyTradingResultPath(instrument, from, to))
            .fetch(this::fetchHistoryBatch)
            .build()
            .readAllPage();
    }

    @Override
    public List<JsonNode> fetchIntradayValues(Instrument instrument, int start) {
        return PageReader
            .builder()
            .start(start)
            .pageSize(5000)
            .url(intradayPath(instrument))
            .fetch(this::fetchIntradayBatch)
            .build()
            .readAllPage();
    }

    private String fetch(String path) {
        try {
            return defaultClient.get()
                .uri(path)
                .retrieve()
                .body(String.class);
        } catch (Exception ex) {
            throw new RestClientException(ex.getMessage(), ex.getCause());
        }
    }

    @SneakyThrows
    private JsonNode fetchIntradayBatch(String path) {
        return objectMapper.readTree(fetch(path)).get("trades");
    }

    @SneakyThrows
    private JsonNode fetchHistoryBatch(String path) {
        return objectMapper.readTree(fetch(path)).get("history");
    }

    private String intradayPath(Instrument instrument) {
        return queryBuilder
            .getFetchDealBy(
                instrument.getClass(),
                instrument.getTicker()
            );
    }

    private String dailyTradingResultPath(Instrument instrument, LocalDate from, LocalDate to) {
        return queryBuilder
            .fetchTradingResultsBy(
                instrument.getClass(),
                instrument.getTicker(),
                from,
                to
            );
    }
}
