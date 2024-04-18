package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.IntradayBatch;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Instrument {
    InstrumentId id;
    String ticker;
    String shortName;
    String name;
    @NonFinal
    Boolean updatable;
    @NonFinal
    LocalDate lastHistoryDate;
    @NonFinal
    Long lastTradingNumber;

    public Instrument(
        InstrumentId id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        setId(id);
        setTicker(ticker);
        setShortName(shortName);
        setName(name);
        setUpdatable(updatable);
        setLastHistoryDate(lastHistoryDate);
        setLastTradingNumber(lastTradingNumber);
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

    private void setLastHistoryDate(LocalDate lastHistoryDate) {
        this.lastHistoryDate = lastHistoryDate;
    }

    private void setLastTradingNumber(Long lastTradingNumber) {
        this.lastTradingNumber = lastTradingNumber;
    }

    private void setId(InstrumentId id) {
        if (id == null) {
            throw new DomainException("Не заполнен тикер инструмента.");
        }
        this.id = id;
    }


    private void setTicker(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            throw new DomainException("Не заполнен тикер инструмента.");
        }
        this.ticker = ticker;
    }

    private void setShortName(String shortName) {
        if (shortName == null || shortName.isBlank()) {
            throw new DomainException("Не заполнено краткое наименование инструмента.");
        }
        this.shortName = shortName;
    }

    private void setUpdatable(Boolean updatable) {
        this.updatable = !Objects.isNull(updatable) && updatable;
    }

    private void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Не заполнено полное наименование инструмента.");
        }
        this.name = name;
    }

    public void recalcSummary(HistoryBatch history, IntradayBatch intraday) {
        intraday.getLastNumber().ifPresent(this::updateLastTradingNumber);
        history.getLastDate().ifPresent(this::updateLastHistoryDate);
    }
}
