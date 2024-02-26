package ru.ioque.investfund.adapters.exchange.moex.client.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MoexValueExtractor {
    Map<String, Integer> indexToColumnName = new HashMap<>();

    public MoexValueExtractor(JsonNode node) {
        if (node != null && node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                indexToColumnName.put(node.get(i).asText(), i);
            }
        }
    }

    public Integer getIndex(String columnName) {
        return indexToColumnName.get(columnName);
    }

    public JsonNode extractValue(JsonNode row, String column) {
        return row.get(getIndex(column));
    }
}
