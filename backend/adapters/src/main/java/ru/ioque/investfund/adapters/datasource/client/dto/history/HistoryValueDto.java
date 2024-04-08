package ru.ioque.investfund.adapters.datasource.client.dto.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryValueDto {
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double highPrice;
    Double lowPrice;
    Double waPrice;
    Double value;

    public HistoryValue toDomain(UUID datasourceId) {
        return HistoryValue.builder()
            .datasourceId(datasourceId)
            .tradeDate(tradeDate)
            .ticker(ticker)
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(highPrice)
            .lowPrice(lowPrice)
            .waPrice(waPrice)
            .value(value)
            .build();
    }
}
