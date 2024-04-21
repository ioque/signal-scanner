package ru.ioque.investfund.domain.scanner.value;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Builder
@ToString
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradingSnapshot {
    LocalDateTime dateTime;
    Ticker ticker;
    Double lastPrice;
    Double firstPrice;
    Double value;
    List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries;
    List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries;

    public TradingSnapshot(
        LocalDateTime dateTime,
        Ticker ticker,
        Double lastPrice,
        Double openPrice,
        Double value,
        List<TimeSeriesValue<Double, ChronoLocalDate>> closePriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> openPriceSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> valueSeries,
        List<TimeSeriesValue<Double, ChronoLocalDate>> waPriceSeries
    ) {
        this.dateTime = dateTime;
        this.ticker = ticker;
        this.lastPrice = lastPrice;
        this.firstPrice = openPrice;
        this.value = value;
        this.closePriceSeries = closePriceSeries;
        this.openPriceSeries = openPriceSeries;
        this.valueSeries = valueSeries;
        this.waPriceSeries = waPriceSeries;
    }

    public Optional<Double> getHistoryMedianValue(int period) {
        if (valueSeries.isEmpty()) return Optional.empty();
        if (valueSeries.size() < period) return Optional.empty();
        var sortedValues = valueSeries.stream().mapToDouble(TimeSeriesValue::getValue).sorted().toArray();
        var n = sortedValues.length;
        if (n % 2 != 0) {
            return Optional.of(sortedValues[(n / 2)]);
        }
        return Optional.of((sortedValues[(n - 1) / 2] + sortedValues[(n / 2)]) / 2.0);
    }

    public Optional<Boolean> isRiseToday() {
        var prevClosePrice = getPrevClosePrice();
        if (lastPrice == null) {
            return Optional.empty();
        }
        return prevClosePrice.map(aDouble -> lastPrice > aDouble && lastPrice > firstPrice);
    }

    public boolean isRiseOvernight(double scale) {
        var prevClosePrice = getPrevClosePrice();
        if (lastPrice == null) {
            return false;
        }
        return prevClosePrice.filter(aDouble -> ((lastPrice / aDouble) - 1) > scale).isPresent();
    }

    public boolean isRiseForPrevDay(double scale) {
        var prevClosePrice = getPrevClosePrice();
        var prevPrevClosePrice = getPrevPrevClosePrice();
        if (prevPrevClosePrice.isEmpty() || prevClosePrice.isEmpty()) return false;
        return ((prevClosePrice.get() / prevPrevClosePrice.get()) - 1) > scale;
    }

    public boolean isRiseForToday(double scale) {
        if (lastPrice == null) {
            return false;
        }
        return ((lastPrice / firstPrice) - 1) > scale;
    }

    public boolean isRiseInLastTwoDay(double historyScale, double intradayScale) {
        return isRiseForPrevDay(historyScale) && isRiseForToday(intradayScale);
    }

    public Optional<Double> getPrevPrevClosePrice() {
        final Optional<LocalDate> lastTradingDate = closePriceSeries.stream().max(TimeSeriesValue::compareTo).map(
            TimeSeriesValue::getTime).map(LocalDate.class::cast);
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
        return ticker.getValue().length() == 5;
    }

    public boolean isSimplePair(TradingSnapshot tradingSnapshot) {
        return ticker
            .getValue()
            .substring(0, ticker.getValue().length() - 1)
            .equals(tradingSnapshot.getTicker().getValue());
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
}
