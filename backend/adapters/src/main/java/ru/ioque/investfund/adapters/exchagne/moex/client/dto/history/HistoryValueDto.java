package ru.ioque.investfund.adapters.exchagne.moex.client.dto.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.HistoryValue;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryValueDto {
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double highPrice;
    Double lowPrice;
    Double waPrice;
    Double value;

    public HistoryValue toDomain() {
        return HistoryValue.builder()
            .tradeDate(tradeDate)
            .ticker(ticker)
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(highPrice)
            .lowPrice(lowPrice)
            .waPrice(lowPrice)
            .value(lowPrice)
            .build();
    }
}
