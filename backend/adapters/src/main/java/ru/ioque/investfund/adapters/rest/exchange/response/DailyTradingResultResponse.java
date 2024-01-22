package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DailyTradingResultResponse implements Serializable {
    LocalDate tradeDate;
    String ticker;
    Double value;
    Double openPrice;
    Double closePrice;
    public static DailyTradingResultResponse fromDomain(DailyValue dailyValue) {
        return DailyTradingResultResponse.builder()
            .tradeDate(dailyValue.getTradeDate())
            .ticker(dailyValue.getTicker())
            .value(dailyValue.getValue())
            .openPrice(dailyValue.getOpenPrice())
            .closePrice(dailyValue.getClosePrice())
            .build();
    }
}
