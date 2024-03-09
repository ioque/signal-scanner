package ru.ioque.investfund.domain.scanner.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FinInstrument extends Domain {
    String ticker;
    Double buyToSellValueRatio;
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
        Double buyToSellValueRatio,
        List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries,
        List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries,
        List<TimeSeriesValue<Double, LocalTime>> todayValueSeries
    ) {
        super(instrumentId);
        this.ticker = ticker;
        this.buyToSellValueRatio = buyToSellValueRatio;
        this.closePriceSeries = closePriceSeries;
        this.openPriceSeries = openPriceSeries;
        this.valueSeries = valueSeries;
        this.waPriceSeries = waPriceSeries;
        this.todayPriceSeries = todayPriceSeries;
        this.todayValueSeries = todayValueSeries;
    }

    public Double getHistoryMedianValue() {
        var sortedValues = todayValueSeries.stream().sorted().toList();
        var n = sortedValues.size();
        if (n % 2 != 0)
            return sortedValues.get(n / 2).getValue();
        return (sortedValues.get((n - 1) / 2).getValue() + sortedValues.get(n / 2).getValue()) / 2.0;
    }

    public Double getTodayOpenPrice() {
        return todayPriceSeries.stream().min(TimeSeriesValue::compareTo).map(TimeSeriesValue::getValue).orElseThrow();
    }

    public Double getTodayLastPrice() {
        return todayPriceSeries.stream().max(TimeSeriesValue::compareTo).map(TimeSeriesValue::getValue).orElseThrow();
    }

    public Double getTodayValue() {
        return todayValueSeries.stream().mapToDouble(row -> Math.abs(row.getValue())).sum();
    }

    public boolean isRiseToday() {
        return Math.abs(getTodayLastPrice()) > Math.abs(getPrevClosePrice()) && Math.abs(getTodayLastPrice()) > Math.abs(getTodayOpenPrice());
    }

    public boolean isRiseOvernight(double scale) {
        return (Math.abs(getTodayLastPrice() / getPrevClosePrice()) - 1) > scale;
    }

    public boolean isRiseForPrevDay(double scale) {
        return (Math.abs(getPrevClosePrice() / getPrevPrevClosePrice()) - 1)  > scale;
    }

    public boolean isRiseForToday(double scale) {
        return (Math.abs(getTodayLastPrice() / getTodayOpenPrice()) - 1) > scale;
    }

    public boolean isRiseInLastTwoDay(double historyScale, double intradayScale) {
        return isRiseForPrevDay(historyScale) && isRiseForToday(intradayScale);
    }

    public Double getPrevPrevClosePrice() {
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

    public Double getPrevClosePrice() {
        return closePriceSeries
            .stream()
            .max(TimeSeriesValue::compareTo)
            .stream()
            .findFirst()
            .map(TimeSeriesValue::getValue)
            .orElse(0.);
    }

    public boolean isPref() {
        return ticker.length() == 5;
    }

    public boolean isSimplePair(FinInstrument finInstrument) {
        return ticker.substring(0, ticker.length() - 1).equals(finInstrument.getTicker());
    }
}
