package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.archive.historyvalue.ArchivedHistoryValueEntity;
import ru.ioque.investfund.domain.exchange.value.HistoryValue;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DailyValueResponse implements Serializable {
    String tradeDate;
    String ticker;
    Double value;
    Double openPrice;
    Double closePrice;
    public static DailyValueResponse fromDomain(HistoryValue historyValue) {
        return DailyValueResponse.builder()
            .tradeDate(historyValue.getTradeDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .ticker(historyValue.getTicker())
            .value(historyValue.getValue())
            .openPrice(historyValue.getOpenPrice())
            .closePrice(historyValue.getClosePrice())
            .build();
    }

    public static DailyValueResponse fromEntity(ArchivedHistoryValueEntity dailyValueEntity) {
        return DailyValueResponse.builder()
            .tradeDate(dailyValueEntity.getTradeDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .ticker(dailyValueEntity.getTicker())
            .value(dailyValueEntity.getValue())
            .openPrice(dailyValueEntity.getOpenPrice())
            .closePrice(dailyValueEntity.getClosePrice())
            .build();
    }
}
