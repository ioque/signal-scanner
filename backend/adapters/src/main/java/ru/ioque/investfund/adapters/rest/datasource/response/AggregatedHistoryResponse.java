package ru.ioque.investfund.adapters.rest.datasource.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.AggregatedHistoryEntity;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AggregatedHistoryResponse implements Serializable {
    String tradeDate;
    String ticker;
    Double value;
    Double openPrice;
    Double closePrice;
    public static AggregatedHistoryResponse fromDomain(AggregatedHistoryEntity aggregatedHistoryEntity) {
        return AggregatedHistoryResponse.builder()
            .tradeDate(aggregatedHistoryEntity.getId().getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .ticker(aggregatedHistoryEntity.getInstrument().getTicker())
            .value(aggregatedHistoryEntity.getValue())
            .openPrice(aggregatedHistoryEntity.getOpenPrice())
            .closePrice(aggregatedHistoryEntity.getClosePrice())
            .build();
    }
}
