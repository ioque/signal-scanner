package ru.ioque.investfund.adapters.exchagne.moex.client.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.CurrencyPair;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.FuturesDealResult;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.exchange.value.IndexDeltaResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DailyTradingResultMoexParser {

    public List<DailyValue> parse(List<JsonNode> batches, Class<? extends Instrument> type) {
        return batches
            .stream()
            .map(row -> parseBatch(row, type))
            .flatMap(Collection::stream)
            .toList();
    }

    public List<DailyValue> parseBatch(final JsonNode jsonNode, Class<? extends Instrument> type) {
        final MoexValueExtractor moexValueExtractor = new MoexValueExtractor(jsonNode.get("columns"));
        final JsonNode rows = jsonNode.get("data");
        List<DailyValue> results = new ArrayList<>();
        if (Objects.nonNull(rows) && rows.isArray() && !rows.isEmpty()) {
            for (int i = 0; i < rows.size(); i++) {
                results.add(mappers.get(type).apply(rows.get(i), moexValueExtractor));
            }
        }
        return results;
    }

    Map<Class<? extends Instrument>, BiFunction<JsonNode, MoexValueExtractor, DailyValue>> mappers = Map.of(
        Stock.class, this::createDealResult,
        CurrencyPair.class, this::createCurrencyDealResult,
        Index.class, this::createDeltaResult,
        Futures.class, this::createContractResult
    );

    private DailyValue createCurrencyDealResult(JsonNode row, MoexValueExtractor moexValueExtractor) {
        return DealResult.builder()
            .tradeDate(LocalDate.parse(moexValueExtractor.extractValue(row, "TRADEDATE").asText()))
            .ticker(moexValueExtractor.extractValue(row, "SECID").asText())
            .openPrice(moexValueExtractor.extractValue(row, "OPEN").asDouble())
            .closePrice(moexValueExtractor.extractValue(row, "CLOSE").asDouble())
            .minPrice(moexValueExtractor.extractValue(row, "LOW").asDouble())
            .maxPrice(moexValueExtractor.extractValue(row, "HIGH").asDouble())
            .value(moexValueExtractor.extractValue(row, "VOLRUR").asDouble())
            .waPrice(moexValueExtractor.extractValue(row, "WAPRICE").asDouble())
            .numTrades(moexValueExtractor.extractValue(row, "NUMTRADES").asDouble())
            .build();
    }

    private DailyValue createDealResult(JsonNode row, MoexValueExtractor moexValueExtractor) {
        return DealResult.builder()
            .tradeDate(LocalDate.parse(moexValueExtractor.extractValue(row, "TRADEDATE").asText()))
            .ticker(moexValueExtractor.extractValue(row, "SECID").asText())
            .openPrice(moexValueExtractor.extractValue(row, "OPEN").asDouble())
            .closePrice(moexValueExtractor.extractValue(row, "CLOSE").asDouble())
            .minPrice(moexValueExtractor.extractValue(row, "LOW").asDouble())
            .maxPrice(moexValueExtractor.extractValue(row, "HIGH").asDouble())
            .value(moexValueExtractor.extractValue(row, "VALUE").asDouble())
            .waPrice(moexValueExtractor.extractValue(row, "WAPRICE").asDouble())
            .numTrades(moexValueExtractor.extractValue(row, "NUMTRADES").asDouble())
            .build();
    }

    private DailyValue createDeltaResult(JsonNode row, MoexValueExtractor moexValueExtractor) {
        return IndexDeltaResult.builder()
            .tradeDate(LocalDate.parse(moexValueExtractor.extractValue(row, "TRADEDATE").asText()))
            .ticker(moexValueExtractor.extractValue(row, "SECID").asText())
            .openPrice(moexValueExtractor.extractValue(row, "OPEN").asDouble())
            .closePrice(moexValueExtractor.extractValue(row, "CLOSE").asDouble())
            .minPrice(moexValueExtractor.extractValue(row, "LOW").asDouble())
            .maxPrice(moexValueExtractor.extractValue(row, "HIGH").asDouble())
            .value(moexValueExtractor.extractValue(row, "VALUE").asDouble())
            .capitalization(moexValueExtractor.extractValue(row, "CAPITALIZATION").asDouble())
            .build();
    }

    private DailyValue createContractResult(JsonNode row, MoexValueExtractor moexValueExtractor) {
        return FuturesDealResult.builder()
            .tradeDate(LocalDate.parse(moexValueExtractor.extractValue(row, "TRADEDATE").asText()))
            .ticker(moexValueExtractor.extractValue(row, "SECID").asText())
            .openPrice(moexValueExtractor.extractValue(row, "OPEN").asDouble())
            .closePrice(moexValueExtractor.extractValue(row, "CLOSE").asDouble())
            .minPrice(moexValueExtractor.extractValue(row, "LOW").asDouble())
            .maxPrice(moexValueExtractor.extractValue(row, "HIGH").asDouble())
            .value(moexValueExtractor.extractValue(row, "VALUE").asDouble())
            .openPositionValue(moexValueExtractor.extractValue(row, "OPENPOSITIONVALUE").asDouble())
            .volume(moexValueExtractor.extractValue(row, "VOLUME").asInt())
            .build();
    }
}
