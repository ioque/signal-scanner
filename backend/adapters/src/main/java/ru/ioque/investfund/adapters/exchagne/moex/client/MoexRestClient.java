package ru.ioque.investfund.adapters.exchagne.moex.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.InstrumentDto;
import ru.ioque.investfund.adapters.exchagne.moex.client.parser.DailyTradingResultMoexParser;
import ru.ioque.investfund.adapters.exchagne.moex.client.parser.InstrumentMoexParser;
import ru.ioque.investfund.adapters.exchagne.moex.client.parser.IntradayValueMoexParser;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MoexRestClient {
    QueryBuilder queryBuilder;
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    RestClient defaultClient = RestClient.create();

    @SneakyThrows
    public List<InstrumentDto> fetchInstruments(Class<? extends Instrument> type) {
        return new InstrumentMoexParser()
            .parse(objectMapper
                .readTree(fetch(queryBuilder.getSecurityListQuery(type)))
                .get("securities"),
                type
            );
    }

    @SneakyThrows
    public List<IntradayValue> fetchIntradayValues(Instrument instrument) {
        return new IntradayValueMoexParser().parse(fetchIntradayValueBatches(instrument), instrument.getClass());
    }

    @SneakyThrows
    public List<DailyValue> fetchDailyTradingResults(
        Instrument instrument,
        LocalDate from,
        LocalDate to
    ) {
        return new DailyTradingResultMoexParser().parse(fetchDailyTradingResultBatches(instrument, from, to), instrument.getClass());
    }

    //сделать валидацию входящих параметров. Доступные размеры стран и тд
    private List<JsonNode> fetchDailyTradingResultBatches(Instrument instrument, LocalDate from, LocalDate to) {
        return PageReader
            .builder()
            .pageSize(100)
            .url(dailyTradingResultPath(instrument, from, to))
            .fetch(this::fetchDailyTradingResultsBatch)
            .build()
            .readAllPage();
    }

    private List<JsonNode> fetchIntradayValueBatches(Instrument instrument) {
        return PageReader
            .builder()
            .pageSize(5000)
            .url(intradayPath(instrument, instrument.getLastDealNumber().orElse(0L)))
            .fetch(this::fetchIntradayValueBatch)
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
    private JsonNode fetchIntradayValueBatch(String path) {
        return objectMapper.readTree(fetch(path)).get("trades");
    }

    @SneakyThrows
    private JsonNode fetchDailyTradingResultsBatch(String path) {
        return objectMapper.readTree(fetch(path)).get("history");
    }

    private String intradayPath(Instrument instrument, Long tradeNo) {
        return queryBuilder
            .getFetchDealBy(
                instrument.getClass(),
                instrument.getTicker()
            )
            + (tradeNo != null && tradeNo > 0 ? ("&tradeno=" + tradeNo + "&") : "");
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

