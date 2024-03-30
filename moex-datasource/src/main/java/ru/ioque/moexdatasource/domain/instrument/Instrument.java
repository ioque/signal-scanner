package ru.ioque.moexdatasource.domain.instrument;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.history.HistoryValue;
import ru.ioque.moexdatasource.domain.intraday.IntradayValue;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Instrument {
    String ticker;
    String shortName;
    String name;

    public String path() {
        return "";
    }

    public List<IntradayValue> parseIntradayValues(JsonNode nodes) {
        return List.of();
    }

    public List<HistoryValue> parseHistoryValues(JsonNode nodes) {
        return List.of();
    }
}
