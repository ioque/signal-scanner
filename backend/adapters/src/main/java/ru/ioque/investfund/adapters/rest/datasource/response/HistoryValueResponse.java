package ru.ioque.investfund.adapters.rest.datasource.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryValueResponse implements Serializable {
    String tradeDate;
    String ticker;
    Double value;
    Double openPrice;
    Double closePrice;
    public static HistoryValueResponse fromDomain(HistoryValueEntity historyValue) {
        return HistoryValueResponse.builder()
            .tradeDate(historyValue.getTradeDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .ticker(historyValue.getTicker())
            .value(historyValue.getValue())
            .openPrice(historyValue.getOpenPrice())
            .closePrice(historyValue.getClosePrice())
            .build();
    }
}
