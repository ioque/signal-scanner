package ru.ioque.investfund.domain.scanner.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FinInstrument extends Domain {
    String ticker;
    List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries;
    List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries;
    List<TimeSeriesValue<Double, LocalTime>> todayValueSeries;


    @Builder
    public FinInstrument(
        UUID instrumentId,
        String ticker,
        List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries,
        List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries,
        List<TimeSeriesValue<Double, LocalTime>> todayValueSeries
    ) {
        super(instrumentId);
        this.ticker = ticker;
        this.closePriceSeries = closePriceSeries;
        this.openPriceSeries = openPriceSeries;
        this.valueSeries = valueSeries;
        this.waPriceSeries = waPriceSeries;
        this.todayPriceSeries = todayPriceSeries;
        this.todayValueSeries = todayValueSeries;
    }

    public Optional<Double> getHistoryMedianValue() {
        if (valueSeries.size() == 1) return Optional.of(todayValueSeries.get(0).getValue());
        if (valueSeries.isEmpty()) return Optional.empty();
        var sortedValues = valueSeries.stream().mapToDouble(TimeSeriesValue::getValue).sorted().toArray();
        var n = sortedValues.length;
        if (n % 2 != 0)
            return Optional.of(sortedValues[(n / 2)]);
        return Optional.of((sortedValues[(n - 1) / 2] + sortedValues[(n / 2)]) / 2.0);
    }

    public Optional<Double> getTodayOpenPrice() {
        return todayPriceSeries.stream().min(TimeSeriesValue::compareTo).map(TimeSeriesValue::getValue);
    }

    public Optional<Double> getTodayLastPrice() {
        return todayPriceSeries.stream().max(TimeSeriesValue::compareTo).map(TimeSeriesValue::getValue);
    }

    public Optional<Double> getTodayValue() {
        if (todayValueSeries.isEmpty()) return Optional.empty();
        return Optional.of(todayValueSeries.stream().mapToDouble(TimeSeriesValue::getValue).sum());
    }

    public boolean isRiseToday() {
        var todayLastPrice = getTodayLastPrice();
        var todayOpenPrice = getTodayOpenPrice();
        var prevClosePrice = getPrevClosePrice();
        if (todayLastPrice.isEmpty() || todayOpenPrice.isEmpty() || prevClosePrice.isEmpty()) return false;
        return todayLastPrice.get() > prevClosePrice.get() && todayLastPrice.get() > todayOpenPrice.get();
    }

    public boolean isRiseOvernight(double scale) {
        var todayLastPrice = getTodayLastPrice();
        var prevClosePrice = getPrevClosePrice();
        if (todayLastPrice.isEmpty() || prevClosePrice.isEmpty()) return false;
        return ((todayLastPrice.get() / prevClosePrice.get()) - 1) > scale;
    }

    public boolean isRiseForPrevDay(double scale) {
        var prevClosePrice = getPrevClosePrice();
        var prevPrevClosePrice = getPrevPrevClosePrice();
        if (prevPrevClosePrice.isEmpty() || prevClosePrice.isEmpty()) return false;
        return ((prevClosePrice.get() / prevPrevClosePrice.get()) - 1)  > scale;
    }

    public boolean isRiseForToday(double scale) {
        var todayLastPrice = getTodayLastPrice();
        var todayOpenPrice = getTodayOpenPrice();
        if (todayLastPrice.isEmpty() || todayOpenPrice.isEmpty()) return false;
        return ((todayLastPrice.get() / todayOpenPrice.get()) - 1) > scale;
    }

    public boolean isRiseInLastTwoDay(double historyScale, double intradayScale) {
        return isRiseForPrevDay(historyScale) && isRiseForToday(intradayScale);
    }

    public Optional<Double> getPrevPrevClosePrice() {
        final LocalDate lastTradingDate = closePriceSeries.stream().max(TimeSeriesValue::compareTo).map(TimeSeriesValue::getTime).map(LocalDate.class::cast).orElseThrow();
        final LocalDate prevLastTradingDate = getPrevTradingDate(lastTradingDate);
        return closePriceSeries.stream()
            .filter(row -> row.getTime().equals(prevLastTradingDate))
            .findFirst()
            .map(TimeSeriesValue::getValue);
    }

    private static LocalDate getPrevTradingDate(LocalDate tradingDate) {
        LocalDate day = tradingDate.minusDays(1);
        if (day.getDayOfWeek().equals(DayOfWeek.SUNDAY)) day = day.minusDays(2);
        if (day.getDayOfWeek().equals(DayOfWeek.SATURDAY)) day = day.minusDays(1);
        return day;
    }

    public Optional<Double> getPrevClosePrice() {
        return closePriceSeries
            .stream()
            .max(TimeSeriesValue::compareTo)
            .stream()
            .findFirst()
            .map(TimeSeriesValue::getValue);
    }

    public boolean isPref() {
        return ticker.length() == 5;
    }

    public boolean isSimplePair(FinInstrument finInstrument) {
        return ticker.substring(0, ticker.length() - 1).equals(finInstrument.getTicker());
    }
}
