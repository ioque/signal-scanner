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
    Double todayValue;
    Double historyMedianValue;
    Double todayLastPrice;
    Double todayOpenPrice;
    Double buyToSellValuesRatio;
    List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries;
    List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries;

    @Builder
    public FinInstrument(
        UUID instrumentId,
        String ticker,
        Double todayValue,
        Double historyMedianValue,
        Double todayLastPrice,
        Double todayOpenPrice,
        Double buyToSellValuesRatio,
        List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries,
        List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries
    ) {
        super(instrumentId);
        this.ticker = ticker;
        this.todayValue = todayValue;
        this.historyMedianValue = historyMedianValue;
        this.todayLastPrice = todayLastPrice;
        this.todayOpenPrice = todayOpenPrice;
        this.buyToSellValuesRatio = buyToSellValuesRatio;
        this.closePriceSeries = closePriceSeries;
        this.openPriceSeries = openPriceSeries;
        this.valueSeries = valueSeries;
        this.waPriceSeries = waPriceSeries;
        this.todayPriceSeries = todayPriceSeries;
    }

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
            .orElse(0.);
    }

    public boolean isPref() {
        return ticker.length() == 5;
    }

    public boolean isSimplePair(FinInstrument finInstrument) {
        return ticker.substring(0, ticker.length() - 1).equals(finInstrument.getTicker());
    }
}
