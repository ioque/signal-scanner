package ru.ioque.moexdatasource.domain.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.instrument.CurrencyPair;
import ru.ioque.moexdatasource.domain.instrument.Futures;
import ru.ioque.moexdatasource.domain.instrument.Index;
import ru.ioque.moexdatasource.domain.instrument.Instrument;
import ru.ioque.moexdatasource.domain.instrument.Stock;
import ru.ioque.moexdatasource.domain.intraday.Contract;
import ru.ioque.moexdatasource.domain.intraday.Deal;
import ru.ioque.moexdatasource.domain.intraday.Delta;
import ru.ioque.moexdatasource.domain.intraday.IntradayValue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntradayValueMoexParser {
    final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    MoexValueExtractor extractor;

    public List<IntradayValue> parse(List<JsonNode> batches, Instrument instrument) {
        return batches
            .stream()
            .map(batch -> parseBatch(batch, instrument))
            .flatMap(Collection::stream)
            .toList();
    }

    public List<IntradayValue> parseBatch(final JsonNode jsonNode, Instrument instrument) {
        this.extractor = new MoexValueExtractor(jsonNode.get("columns"));
        final JsonNode rows = jsonNode.get("data");
        List<IntradayValue> results = new ArrayList<>();
        if (rows.isArray()) {
            for (int i = 0; i < rows.size(); i++) {
                results
                    .add(
                        mappers
                            .get(instrument.getClass())
                            .apply(rows.get(i), instrument)
                    );
            }
        }
        return results;
    }

    final Map<Class<? extends Instrument>, BiFunction<JsonNode, Instrument, IntradayValue>> mappers = Map.of(
        Stock.class, this::createDeal,
        CurrencyPair.class, this::createDeal,
        Index.class, this::createDelta,
        Futures.class, this::createContract
    );

    private IntradayValue createContract(JsonNode node, Instrument instrument) {
        return Contract.builder()
            .dateTime(parseDateTime(node, extractor))
            .ticker(extractor.extractValue(node, "SECID").asText())
            .qnt(extractor.extractValue(node, "QUANTITY").asInt())
            .price(extractor.extractValue(node, "PRICE").asDouble())
            .tradeNumber(extractor.extractValue(node, "TRADENO").asLong())
            .board(extractor.extractValue(node, "BOARDNAME").asText())
            .value(
                ((Futures) instrument).getLotVolume() *
                extractor.extractValue(node, "PRICE").asDouble() *
                extractor.extractValue(node, "QUANTITY").asInt())
            .build();
    }

    private IntradayValue createDelta(JsonNode node, Instrument instrument) {
        return Delta.builder()
            .dateTime(parseDateTime(node, extractor))
            .ticker(extractor.extractValue(node, "SECID").asText())
            .value(extractor.extractValue(node, "VALUE").asDouble())
            .price(extractor.extractValue(node, "PRICE").asDouble())
            .tradeNumber(extractor.extractValue(node, "TRADENO").asLong())
            .board(extractor.extractValue(node, "BOARDID").asText())
            .build();
    }

    private IntradayValue createDeal(JsonNode node, Instrument instrument) {
        return Deal.builder()
            .dateTime(parseDateTime(node, extractor))
            .ticker(extractor.extractValue(node, "SECID").asText())
            .value(extractor.extractValue(node, "VALUE").asDouble())
            .isBuy(extractor.extractValue(node, "BUYSELL").asText().equals("BUY"))
            .qnt(extractor.extractValue(node, "QUANTITY").asInt())
            .price(extractor.extractValue(node, "PRICE").asDouble())
            .tradeNumber(extractor.extractValue(node, "TRADENO").asLong())
            .board(extractor.extractValue(node, "BOARDID").asText())
            .build();
    }

    private LocalDateTime parseDateTime(JsonNode node, MoexValueExtractor extractor) {
        return LocalDateTime
            .parse(extractor.extractValue(node, "SYSTIME").asText(), FORMATTER)
            .toLocalDate()
            .atTime(LocalTime.parse(extractor.extractValue(node, "TRADETIME").asText()));
    }
}
