package ru.ioque.investfund.domain.exchange.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
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
    TreeSet<IntradayValue> intradayValues;
    TreeSet<DailyValue> dailyValues;

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
        this.intradayValues = intradayValues == null ? new TreeSet<>() : new TreeSet<>(intradayValues);
        this.dailyValues = dailyValues == null ? new TreeSet<>() : new TreeSet<>(dailyValues);
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
        if (this.intradayValues.isEmpty()) return Optional.empty();
        return Optional.of(this.intradayValues.last());
    }

    public Optional<Long> getLastDealNumber() {
        return lastIntradayValue().map(IntradayValue::getNumber);
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
}
