package ru.ioque.moexdatasource.domain.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.history.HistoryValue;
import ru.ioque.moexdatasource.domain.instrument.CurrencyPair;
import ru.ioque.moexdatasource.domain.instrument.Futures;
import ru.ioque.moexdatasource.domain.instrument.Index;
import ru.ioque.moexdatasource.domain.instrument.Instrument;
import ru.ioque.moexdatasource.domain.instrument.Stock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryValueMoexParser {

    public List<HistoryValue> parse(List<JsonNode> batches, Class<? extends Instrument> type) {
        return batches
            .stream()
            .map(row -> parseBatch(row, type))
            .flatMap(Collection::stream)
            .toList();
    }

    public List<HistoryValue> parseBatch(final JsonNode jsonNode, Class<? extends Instrument> type) {
        final MoexValueExtractor moexValueExtractor = new MoexValueExtractor(jsonNode.get("columns"));
        final JsonNode rows = jsonNode.get("data");
        List<HistoryValue> results = new ArrayList<>();
        if (Objects.nonNull(rows) && rows.isArray() && !rows.isEmpty()) {
            for (int i = 0; i < rows.size(); i++) {
                results.add(mappers.get(type).apply(rows.get(i), moexValueExtractor));
            }
        }
        return results;
    }

    Map<Class<? extends Instrument>, BiFunction<JsonNode, MoexValueExtractor, HistoryValue>> mappers = Map.of(
        Stock.class, this::createDealResult,
        CurrencyPair.class, this::createCurrencyDealResult,
        Index.class, this::createDeltaResult,
        Futures.class, this::createContractResult
    );

    private HistoryValue createCurrencyDealResult(JsonNode row, MoexValueExtractor moexValueExtractor) {
        return HistoryValue.builder()
            .tradeDate(LocalDate.parse(moexValueExtractor.extractValue(row, "TRADEDATE").asText()))
            .ticker(moexValueExtractor.extractValue(row, "SECID").asText())
            .openPrice(moexValueExtractor.extractValue(row, "OPEN").asDouble())
            .closePrice(moexValueExtractor.extractValue(row, "CLOSE").asDouble())
            .minPrice(moexValueExtractor.extractValue(row, "LOW").asDouble())
            .maxPrice(moexValueExtractor.extractValue(row, "HIGH").asDouble())
            .value(moexValueExtractor.extractValue(row, "VOLRUR").asDouble())
            .waPrice(moexValueExtractor.extractValue(row, "WAPRICE").asDouble())
            .build();
    }

    private HistoryValue createDealResult(JsonNode row, MoexValueExtractor moexValueExtractor) {
        return HistoryValue.builder()
            .tradeDate(LocalDate.parse(moexValueExtractor.extractValue(row, "TRADEDATE").asText()))
            .ticker(moexValueExtractor.extractValue(row, "SECID").asText())
            .openPrice(moexValueExtractor.extractValue(row, "OPEN").asDouble())
            .closePrice(moexValueExtractor.extractValue(row, "CLOSE").asDouble())
            .minPrice(moexValueExtractor.extractValue(row, "LOW").asDouble())
            .maxPrice(moexValueExtractor.extractValue(row, "HIGH").asDouble())
            .value(moexValueExtractor.extractValue(row, "VALUE").asDouble())
            .waPrice(moexValueExtractor.extractValue(row, "WAPRICE").asDouble())
            .build();
    }

    private HistoryValue createDeltaResult(JsonNode row, MoexValueExtractor moexValueExtractor) {
        return HistoryValue.builder()
            .tradeDate(LocalDate.parse(moexValueExtractor.extractValue(row, "TRADEDATE").asText()))
            .ticker(moexValueExtractor.extractValue(row, "SECID").asText())
            .openPrice(moexValueExtractor.extractValue(row, "OPEN").asDouble())
            .closePrice(moexValueExtractor.extractValue(row, "CLOSE").asDouble())
            .minPrice(moexValueExtractor.extractValue(row, "LOW").asDouble())
            .maxPrice(moexValueExtractor.extractValue(row, "HIGH").asDouble())
            .value(moexValueExtractor.extractValue(row, "VALUE").asDouble())
            .build();
    }

    private HistoryValue createContractResult(JsonNode row, MoexValueExtractor moexValueExtractor) {
        return HistoryValue.builder()
            .tradeDate(LocalDate.parse(moexValueExtractor.extractValue(row, "TRADEDATE").asText()))
            .ticker(moexValueExtractor.extractValue(row, "SECID").asText())
            .openPrice(moexValueExtractor.extractValue(row, "OPEN").asDouble())
            .closePrice(moexValueExtractor.extractValue(row, "CLOSE").asDouble())
            .minPrice(moexValueExtractor.extractValue(row, "LOW").asDouble())
            .maxPrice(moexValueExtractor.extractValue(row, "HIGH").asDouble())
            .value(moexValueExtractor.extractValue(row, "VALUE").asDouble())
            .build();
    }
}
