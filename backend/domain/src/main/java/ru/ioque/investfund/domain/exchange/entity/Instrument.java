package ru.ioque.investfund.domain.exchange.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ioque.investfund.domain.Domain;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;
import ru.ioque.investfund.domain.exchange.value.statistic.TimeSeriesValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class Instrument extends Domain {
    String ticker;
    String shortName;
    String name;
    @NonFinal
    Boolean updatable;
    List<IntradayValue> intradayValues;
    List<DailyValue> dailyValues;

    public Instrument(
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        List<IntradayValue> intradayValues,
        List<DailyValue> dailyValues
    ) {
        super(id);
        this.ticker = ticker;
        this.shortName = shortName;
        this.name = name;
        this.updatable = updatable;
        this.intradayValues = intradayValues == null ? new ArrayList<>() : new ArrayList<>(intradayValues);
        this.dailyValues = dailyValues == null ? new ArrayList<>() : new ArrayList<>(dailyValues);
    }

    public void enableUpdate() {
        this.updatable = true;
    }

    public void disableUpdate() {
        this.updatable = false;
    }

    public boolean isUpdatable() {
        return Boolean.TRUE.equals(updatable);
    }

    public void addNewIntradayValue(IntradayValue intradayValue) {
        if (lastIntradayValue().map(intradayValue::isAfter).orElse(true)) {
            this.intradayValues.add(intradayValue);
        }
    }

    public void addNewDailyValue(DailyValue dailyValue) {
        if (lastDailyValue().map(dailyValue::isAfter).orElse(true)) {
            this.dailyValues.add(dailyValue);
        }
    }

    public void addIntradayValue(IntradayValue intradayValue) {
        this.intradayValues.add(intradayValue);
    }

    public void addDailyValue(DailyValue dailyValue) {
        this.dailyValues.add(dailyValue);
    }

    public Optional<DailyValue> lastDailyValue() {
        return this.dailyValues.stream().max(DailyValue::compareTo);
    }

    public Optional<IntradayValue> lastIntradayValue() {
        return this.intradayValues.stream().max(IntradayValue::compareTo);
    }

    public Optional<IntradayValue> firstIntradayValue() {
        return this.intradayValues.stream().min(IntradayValue::compareTo);
    }

    public Optional<Long> getLastDealNumber() {
        return lastIntradayValue().map(IntradayValue::getNumber);
    }

    public Double getTodayValue() {
        return intradayValues
            .stream()
            .mapToDouble(IntradayValue::getValue)
            .sum();
    }

    private Double getHistoryMedianValue() {
        var sortedValues = dailyValues.stream().sorted().toList();
        var n = sortedValues.size();
        if (n % 2 != 0)
            return sortedValues.get(n / 2).getValue();
        return (sortedValues.get((n - 1) / 2).getValue() + sortedValues.get(n / 2).getValue()) / 2.0;
    }

    public Double getLastDealPrice() {
        return lastIntradayValue().map(IntradayValue::getPrice).orElse(0.0);
    }

    public Double getFirstDealPrice() {
        return firstIntradayValue().map(IntradayValue::getPrice).orElse(0.0);
    }

    public String printMarketStat() {
        return String.format(
            "Текущее количество сделок %s, интервал сделок: %s - %s. Текущий период исторических данных: %s - %s.",
            getIntradayValues().size(),
            getIntradayValues()
                .stream()
                .min(IntradayValue::compareTo)
                .map(IntradayValue::getDateTime)
                .map(LocalDateTime::toString)
                .orElse("<_>"),
            getIntradayValues()
                .stream()
                .max(IntradayValue::compareTo)
                .map(IntradayValue::getDateTime)
                .map(LocalDateTime::toString)
                .orElse("<_>"),
            getDailyValues()
                .stream()
                .min(DailyValue::compareTo)
                .map(DailyValue::getTradeDate)
                .map(LocalDate::toString)
                .orElse("<_>"),
            getDailyValues()
                .stream()
                .max(DailyValue::compareTo)
                .map(DailyValue::getTradeDate)
                .map(LocalDate::toString)
                .orElse("<_>")
        );
    }

    public abstract List<TimeSeriesValue<Double, ChronoLocalDate>> getWaPriceSeries();

    public InstrumentStatistic calcStatistic() {
        return InstrumentStatistic
            .builder()
            .instrumentId(getId())
            .ticker(ticker)
            .todayLastPrice(getLastDealPrice())
            .todayOpenPrice(getFirstDealPrice())
            .todayValue(getTodayValue())
            .historyMedianValue(getHistoryMedianValue())
            .closePriceSeries(getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate())).toList())
            .openPriceSeries(getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate())).toList())
            .openPriceSeries(getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate())).toList())
            .valueSeries(getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate())).toList())
            .waPriceSeries(getWaPriceSeries())
            .todayPriceSeries(getIntradayValues().stream().map(intradayValue -> new TimeSeriesValue<>(intradayValue.getPrice(), intradayValue.getDateTime().toLocalTime())).toList())
            .build();
    }
}
