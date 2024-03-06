package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.statistic.InstrumentStatistic;
import ru.ioque.investfund.domain.scanner.financial.entity.TimeSeriesValue;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class InstrumentStatisticResponse {
    Double todayValue;
    Double medianHistoryValue;
    Double todayLastPrice;
    Double todayOpenPrice;
    List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries;

    public static InstrumentStatisticResponse fromDomain(InstrumentStatistic instrumentStatistic) {
        return InstrumentStatisticResponse.builder()
            .todayValue(instrumentStatistic.getTodayValue())
            .medianHistoryValue(instrumentStatistic.getHistoryMedianValue())
            .todayLastPrice(instrumentStatistic.getTodayLastPrice())
            .todayOpenPrice(instrumentStatistic.getTodayOpenPrice())
            .closePriceSeries(instrumentStatistic.getClosePriceSeries())
            .waPriceSeries(instrumentStatistic.getWaPriceSeries())
            .build();
    }
}
