package ru.ioque.investfund.adapters.rest.datasource.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AggregatedHistoryResponse implements Serializable {
    Double value;
    @JsonInclude(Include.NON_NULL)
    Double waPrice;
    Double lowPrice;
    Double highPrice;
    Double openPrice;
    Double closePrice;
    LocalDate tradeDate;

    public static AggregatedHistoryResponse fromDomain(AggregatedTotals aggregatedHistoryEntity) {
        return AggregatedHistoryResponse.builder()
            .value(aggregatedHistoryEntity.getValue())
            .waPrice(aggregatedHistoryEntity.getWaPrice())
            .lowPrice(aggregatedHistoryEntity.getLowPrice())
            .highPrice(aggregatedHistoryEntity.getHighPrice())
            .openPrice(aggregatedHistoryEntity.getOpenPrice())
            .closePrice(aggregatedHistoryEntity.getClosePrice())
            .tradeDate(aggregatedHistoryEntity.getDate())
            .build();
    }
}
