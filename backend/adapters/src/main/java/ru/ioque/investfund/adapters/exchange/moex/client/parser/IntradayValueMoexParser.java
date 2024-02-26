package ru.ioque.investfund.adapters.exchange.moex.client.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.CurrencyPair;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.tradingData.FuturesDeal;
import ru.ioque.investfund.domain.exchange.value.tradingData.Deal;
import ru.ioque.investfund.domain.exchange.value.tradingData.IndexDelta;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntradayValueMoexParser {
    static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<IntradayValue> parse(List<JsonNode> batches, Class<? extends Instrument> type) {
        return batches
            .stream()
            .map(batch -> parseBatch(batch, type))
            .flatMap(Collection::stream)
            .toList();
    }

    public List<IntradayValue> parseBatch(final JsonNode jsonNode, Class<? extends Instrument> type) {
        MoexValueExtractor moexValueExtractor = new MoexValueExtractor(jsonNode.get("columns"));
        final JsonNode rows = jsonNode.get("data");
        List<IntradayValue> results = new ArrayList<>();
        if (rows.isArray()) {
            for (int i = 0; i < rows.size(); i++) {
                results.add(mappers.get(type).apply(rows.get(i), moexValueExtractor));
            }
        }
        return results;
    }

    Map<Class<? extends Instrument>, BiFunction<JsonNode, MoexValueExtractor, IntradayValue>> mappers = Map.of(
        Stock.class, this::createDeal,
        CurrencyPair.class, this::createDeal,
        Index.class, this::createDelta,
        Futures.class, this::createContract
    );

    private IntradayValue createContract(JsonNode node, MoexValueExtractor extractor) {
        return FuturesDeal.builder()
            .dateTime(parseDateTime(node, extractor))
            .ticker(extractor.extractValue(node, "SECID").asText())
            .qnt(extractor.extractValue(node, "QUANTITY").asInt())
            .price(extractor.extractValue(node, "PRICE").asDouble())
            .number(extractor.extractValue(node, "TRADENO").asLong())
            .build();
    }

    private IntradayValue createDelta(JsonNode node, MoexValueExtractor extractor) {
        return IndexDelta.builder()
            .dateTime(parseDateTime(node, extractor))
            .ticker(extractor.extractValue(node, "SECID").asText())
            .value(extractor.extractValue(node, "VALUE").asDouble())
            .price(extractor.extractValue(node, "PRICE").asDouble())
            .number(extractor.extractValue(node, "TRADENO").asLong())
            .build();
    }

    private IntradayValue createDeal(JsonNode node, MoexValueExtractor extractor) {
        return Deal.builder()
            .dateTime(parseDateTime(node, extractor))
            .ticker(extractor.extractValue(node, "SECID").asText())
            .value(extractor.extractValue(node, "VALUE").asDouble())
            .isBuy(extractor.extractValue(node, "BUYSELL").asText().equals("B"))
            .qnt(extractor.extractValue(node, "QUANTITY").asInt())
            .price(extractor.extractValue(node, "PRICE").asDouble())
            .number(extractor.extractValue(node, "TRADENO").asLong())
            .build();
    }

    private LocalDateTime parseDateTime(JsonNode node, MoexValueExtractor extractor) {
        return LocalDateTime
            .parse(extractor.extractValue(node, "SYSTIME").asText(), FORMATTER)
            .toLocalDate()
            .atTime(LocalTime.parse(extractor.extractValue(node, "TRADETIME").asText()));
    }
}
