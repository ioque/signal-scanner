package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.IntradayBatch;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Instrument {
    final InstrumentId id;
    final String shortName;
    final String name;
    Boolean updatable;
    LocalDate lastHistoryDate;
    Long lastTradingNumber;

    public Instrument(
        InstrumentId id,
        String shortName,
        String name,
        Boolean updatable,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        this.id = id;
        this.shortName = shortName;
        this.name = name;
        this.updatable = updatable;
        this.lastHistoryDate = lastHistoryDate;
        this.lastTradingNumber = lastTradingNumber;
    }

    public Optional<LocalDate> getLastHistoryDate() {
        return Optional.ofNullable(lastHistoryDate);
    }

    public LocalDate historyRightBound(LocalDate now) {
        return now.minusDays(1);
    }

    public LocalDate historyLeftBound(LocalDate now) {
        return getLastHistoryDate().map(lastHistoryDate -> lastHistoryDate.plusDays(1)).orElse(now.minusMonths(6));
    }

    public Long getLastTradingNumber() {
        return Optional.ofNullable(lastTradingNumber).orElse(0L);
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

    public void updateLastTradingNumber(Long lastTradingNumber) {
        this.lastTradingNumber = lastTradingNumber;
    }

    public void updateLastHistoryDate(LocalDate lastHistoryDate) {
        this.lastHistoryDate = lastHistoryDate;
    }

    public void recalcSummary(HistoryBatch history, IntradayBatch intraday) {
        intraday.getLastNumber().ifPresent(this::updateLastTradingNumber);
        history.getLastDate().ifPresent(this::updateLastHistoryDate);
    }
}
