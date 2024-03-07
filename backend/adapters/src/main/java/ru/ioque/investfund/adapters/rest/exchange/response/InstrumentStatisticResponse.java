package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.statistic.value.InstrumentStatistic;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class InstrumentStatisticResponse {
    Double todayValue;
    Double medianHistoryValue;
    Double todayLastPrice;
    Double todayOpenPrice;

    public static InstrumentStatisticResponse fromDomain(InstrumentStatistic instrumentStatistic) {
        return InstrumentStatisticResponse.builder()
            .todayValue(instrumentStatistic.getTodayValue())
            .medianHistoryValue(instrumentStatistic.getHistoryMedianValue())
            .todayLastPrice(instrumentStatistic.getTodayLastPrice())
            .todayOpenPrice(instrumentStatistic.getTodayOpenPrice())
            .build();
    }
}
