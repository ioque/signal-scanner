package ru.ioque.moexdatasource.domain.instrument;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class InstrumentParser {
    public List<Instrument> parse(JsonNode jsonNode, Class<? extends Instrument> type) {
        final MoexValueExtractor moexValueExtractor = new MoexValueExtractor(jsonNode.get("columns"));
        final JsonNode rows = jsonNode.get("data");
        List<Instrument> instruments = new ArrayList<>();
        if (rows.isArray()) {
            for (int i = 0; i < rows.size(); i++) {
                instruments.add(mappers.get(type).apply(rows.get(i), moexValueExtractor));
            }
        }
        return instruments;
    }

    Map<Class<? extends Instrument>, BiFunction<JsonNode, MoexValueExtractor, ? extends Instrument>> mappers = Map.of(
        Stock.class, this::createStock,
        CurrencyPair.class, this::createCurrencyPair,
        Index.class, this::createIndex,
        Futures.class, this::createFutures
    );

    private Instrument createFutures(JsonNode node, MoexValueExtractor extractor) {
        return Futures.builder()
            .ticker(extractor.extractValue(node, "SECID").asText())
            .name(extractor.extractValue(node, "SECNAME").asText())
            .shortName(extractor.extractValue(node, "SHORTNAME").asText())
            .assetCode(extractor.extractValue(node, "ASSETCODE").asText())
            .initialMargin(extractor.extractValue(node, "INITIALMARGIN").asDouble())
            .lowLimit(extractor.extractValue(node, "LOWLIMIT").asDouble())
            .highLimit(extractor.extractValue(node, "HIGHLIMIT").asDouble())
            .lotVolume(extractor.extractValue(node, "LOTVOLUME").asInt())
            .build();
    }

    private Instrument createIndex(JsonNode node, MoexValueExtractor extractor) {
        return Index.builder()
            .ticker(extractor.extractValue(node, "SECID").asText())
            .name(extractor.extractValue(node, "NAME").asText())
            .shortName(extractor.extractValue(node, "SHORTNAME").asText())
            .annualHigh(extractor.extractValue(node, "ANNUALHIGH").asDouble())
            .annualLow(extractor.extractValue(node, "ANNUALLOW").asDouble())
            .build();
    }

    private Instrument createCurrencyPair(JsonNode node, MoexValueExtractor extractor) {
        return CurrencyPair.builder()
            .ticker(extractor.extractValue(node, "SECID").asText())
            .name(extractor.extractValue(node, "SECNAME").asText())
            .shortName(extractor.extractValue(node, "SHORTNAME").asText())
            .lotSize(extractor.extractValue(node, "LOTSIZE").asInt())
            .faceUnit(extractor.extractValue(node, "FACEUNIT").asText())
            .build();
    }

    private Instrument createStock(JsonNode node, MoexValueExtractor extractor) {
        return Stock.builder()
            .ticker(extractor.extractValue(node, "SECID").asText())
            .name(extractor.extractValue(node, "SECNAME").asText())
            .shortName(extractor.extractValue(node, "SHORTNAME").asText())
            .lotSize(extractor.extractValue(node, "LOTSIZE").asInt())
            .isin(extractor.extractValue(node, "ISIN").asText())
            .regNumber(extractor.extractValue(node, "REGNUMBER").asText())
            .listLevel(extractor.extractValue(node, "LISTLEVEL").asInt())
            .build();
    }
}
