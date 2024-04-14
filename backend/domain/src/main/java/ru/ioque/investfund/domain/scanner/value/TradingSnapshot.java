package ru.ioque.investfund.domain.scanner.value;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Builder
@ToString
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradingSnapshot {
    String ticker;
    List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries;
    List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries;
    List<TimeSeriesValue<Double, LocalTime>> todayValueSeries;

    public TradingSnapshot(
        String ticker,
        List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries,
        List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries,
        List<TimeSeriesValue<Double, LocalTime>> todayValueSeries
    ) {
        setTicker(ticker);
        setClosePriceSeries(closePriceSeries);
        setOpenPriceSeries(openPriceSeries);
        setValueSeries(valueSeries);
        setWaPriceSeries(waPriceSeries);
        setTodayPriceSeries(todayPriceSeries);
        setTodayValueSeries(todayValueSeries);
    }

    public Optional<Double> getHistoryMedianValue() {
        if (valueSeries.size() == 1) return Optional.of(valueSeries.get(0).getValue());
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

    public Optional<Boolean> isRiseToday() {
        var todayLastPrice = getTodayLastPrice();
        var todayOpenPrice = getTodayOpenPrice();
        var prevClosePrice = getPrevClosePrice();
        if (todayLastPrice.isEmpty() || todayOpenPrice.isEmpty() || prevClosePrice.isEmpty()) return Optional.empty();
        return Optional.of(todayLastPrice.get() > prevClosePrice.get() && todayLastPrice.get() > todayOpenPrice.get());
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
        final Optional<LocalDate> lastTradingDate = closePriceSeries.stream().max(TimeSeriesValue::compareTo).map(TimeSeriesValue::getTime).map(LocalDate.class::cast);
        if (lastTradingDate.isEmpty()) return Optional.empty();
        final LocalDate prevLastTradingDate = getPrevTradingDate(lastTradingDate.get());
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

    public boolean isSimplePair(TradingSnapshot tradingSnapshot) {
        return ticker.substring(0, ticker.length() - 1).equals(tradingSnapshot.getTicker());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TradingSnapshot that = (TradingSnapshot) object;
        return Objects.equals(ticker, that.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }

    private void setTicker(String ticker) {
        if (ticker == null || ticker.isEmpty()) {
            throw new DomainException("Не передан тикер.");
        }
        this.ticker = ticker;
    }

    private void setClosePriceSeries(List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries) {
        this.closePriceSeries = closePriceSeries;
    }

    private void setOpenPriceSeries(List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries) {
        this.openPriceSeries = openPriceSeries;
    }

    private void setValueSeries(List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries) {
        this.valueSeries = valueSeries;
    }

    private void setWaPriceSeries(List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries) {
        this.waPriceSeries = waPriceSeries;
    }

    private void setTodayPriceSeries(List<TimeSeriesValue<Double, LocalTime>> todayPriceSeries) {
        this.todayPriceSeries = todayPriceSeries;
    }

    private void setTodayValueSeries(List<TimeSeriesValue<Double, LocalTime>> todayValueSeries) {
        this.todayValueSeries = todayValueSeries;
    }
}