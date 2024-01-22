package ru.ioque.investfund.domain.exchange.value.statistic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.DomainException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentStatistic {
    UUID instrumentId;
    String ticker;
    Double todayValue;
    Double historyMedianValue;
    Double todayLastPrice;
    Double todayOpenPrice;
    List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries;
    List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries;

    public boolean isRiseToday() {
        return todayLastPrice > getLastClosePrice() && todayLastPrice > todayOpenPrice;
    }

    public boolean isRiseOvernight(double scale) {
        return ((todayLastPrice / getLastClosePrice()) - 1) > scale;
    }

    public boolean isRiseForPrevDay(double scale) {
        return ((getLastClosePrice() / getPrevLastClosePrice()) - 1)  > scale;
    }

    public boolean isRiseForToday(double scale) {
        return ((todayLastPrice / todayOpenPrice) - 1) > scale;
    }

    public boolean isRiseInLastTwoDay(double historyScale, double intradayScale) {
        return isRiseForPrevDay(historyScale) && isRiseForToday(intradayScale);
    }

    public Double getPrevLastClosePrice() {
        final LocalDate lastTradingDate = closePriceSeries.stream().max(TimeSeriesValue::compareTo).map(TimeSeriesValue::getTime).map(LocalDate.class::cast).orElseThrow();
        final LocalDate prevLastTradingDate = getPrevTradingDate(lastTradingDate);
        return closePriceSeries.stream()
            .filter(row -> row.getTime().equals(prevLastTradingDate))
            .findFirst()
            .map(TimeSeriesValue::getValue)
            .orElseThrow(() -> new DomainException("Нет данных по итогам торгов за " + prevLastTradingDate + "."));
    }

    private static LocalDate getPrevTradingDate(LocalDate tradingDate) {
        LocalDate day = tradingDate.minusDays(1);
        if (day.getDayOfWeek().equals(DayOfWeek.SUNDAY)) day = day.minusDays(2);
        if (day.getDayOfWeek().equals(DayOfWeek.SATURDAY)) day = day.minusDays(1);
        return day;
    }

    public Double getLastClosePrice() {
        return closePriceSeries
            .stream()
            .max(TimeSeriesValue::compareTo)
            .stream()
            .findFirst()
            .map(TimeSeriesValue::getValue)
            .orElseThrow(() -> new DomainException("Нет данных по итогам торгов."));
    }

    public boolean isPref() {
        return ticker.length() == 5;
    }

    public boolean isSimplePair(InstrumentStatistic statistic) {
        return ticker.substring(0, ticker.length() - 1).equals(statistic.getTicker());
    }
}
