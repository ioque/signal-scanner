package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

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
    TreeSet<HistoryValue> historyValues;

    public Instrument(
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        List<IntradayValue> intradayValues,
        List<HistoryValue> historyValues
    ) {
        super(id);
        this.ticker = ticker;
        this.shortName = shortName;
        this.name = name;
        this.updatable = updatable;
        this.intradayValues = intradayValues == null ? new TreeSet<>() : new TreeSet<>(intradayValues);
        this.historyValues = historyValues == null ? new TreeSet<>() : new TreeSet<>(historyValues);
    }

    public void enableUpdate() {
        updatable = true;
    }

    public void disableUpdate() {
        updatable = false;
    }

    public boolean isUpdatable() {
        return Boolean.TRUE.equals(updatable);
    }

    public void addIntradayValues(List<IntradayValue> intradayValues) {
        intradayValues.forEach(this::addIntradayValue);
    }

    public void addHistoryValues(List<HistoryValue> historyValues) {
        historyValues.forEach(this::addHistoryValue);
    }

    public Optional<HistoryValue> lastDailyValue() {
        return historyValues.stream().max(HistoryValue::compareTo);
    }

    public Optional<LocalDate> lastHistoryValueDate() {
        return lastDailyValue().map(HistoryValue::getTradeDate);
    }
    public Optional<Long> lastIntradayNumber() {
        return lastIntradayValue().map(IntradayValue::getNumber);
    }

    public boolean isNeedUpdateHistory(LocalDate nowDate) {
        return lastDailyValue()
            .map(HistoryValue::getTradeDate)
            .map(date -> !date.equals(nowDate.minusDays(1)))
            .orElse(true);
    }

    public Optional<IntradayValue> lastIntradayValue() {
        if (intradayValues.isEmpty()) return Optional.empty();
        return Optional.of(intradayValues.last());
    }

    private void addIntradayValue(IntradayValue intradayValue) {
        if (lastIntradayValue().map(intradayValue::isAfter).orElse(true)) {
            intradayValues.add(intradayValue);
        }
    }

    private void addHistoryValue(HistoryValue historyValue) {
        if (lastDailyValue().map(historyValue::isAfter).orElse(true)) {
            historyValues.add(historyValue);
        }
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
            getHistoryValues()
                .stream()
                .min(HistoryValue::compareTo)
                .map(HistoryValue::getTradeDate)
                .map(LocalDate::toString)
                .orElse("<_>"),
            getHistoryValues()
                .stream()
                .max(HistoryValue::compareTo)
                .map(HistoryValue::getTradeDate)
                .map(LocalDate::toString)
                .orElse("<_>")
        );
    }
}
