package ru.ioque.moexdatasource.adapters.rest.response.history;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.history.HistoryValue;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryValueResponse {
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double highPrice;
    Double lowPrice;
    Double waPrice;
    Double value;

    public static HistoryValueResponse from(HistoryValue historyValue) {
        return HistoryValueResponse.builder()
            .ticker(historyValue.getTicker())
            .openPrice(historyValue.getOpenPrice())
            .closePrice(historyValue.getClosePrice())
            .highPrice(historyValue.getMaxPrice())
            .lowPrice(historyValue.getMinPrice())
            .waPrice(historyValue.getWaPrice())
            .value(historyValue.getValue())
            .tradeDate(historyValue.getTradeDate())
            .build();
    }
}
