package ru.ioque.acceptance.domain.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentStatistic {
    Double todayValue;
    Double medianHistoryValue;
    Double todayLastPrice;
    Double todayOpenPrice;
    List<TimeSeriesValue<Double, LocalDate>> closePriceSeries;
    List<TimeSeriesValue<Double, LocalDate>> openPriceSeries;
    List<TimeSeriesValue<Double, LocalDate>> waPriceSeries;
    List<TimeSeriesValue<Double, LocalDate>> valueSeries;
    List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries;

}
