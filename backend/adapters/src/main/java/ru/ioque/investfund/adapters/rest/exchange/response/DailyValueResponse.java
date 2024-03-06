package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.testingsystem.dailyvalue.ArchivedDailyValueEntity;
import ru.ioque.investfund.domain.exchange.value.DailyValue;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DailyValueResponse implements Serializable {
    LocalDate tradeDate;
    String ticker;
    Double value;
    Double openPrice;
    Double closePrice;
    public static DailyValueResponse fromDomain(DailyValue dailyValue) {
        return DailyValueResponse.builder()
            .tradeDate(dailyValue.getTradeDate())
            .ticker(dailyValue.getTicker())
            .value(dailyValue.getValue())
            .openPrice(dailyValue.getOpenPrice())
            .closePrice(dailyValue.getClosePrice())
            .build();
    }

    public static DailyValueResponse fromEntity(ArchivedDailyValueEntity dailyValueEntity) {
        return DailyValueResponse.builder()
            .tradeDate(dailyValueEntity.getTradeDate())
            .ticker(dailyValueEntity.getTicker())
            .value(dailyValueEntity.getValue())
            .openPrice(dailyValueEntity.getOpenPrice())
            .closePrice(dailyValueEntity.getClosePrice())
            .build();
    }
}
