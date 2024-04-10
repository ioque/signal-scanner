package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Instrument extends Domain {
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
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id);
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
        return getLastHistoryDate().orElse(now.minusMonths(6));
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

    public String printMarketStat() {
        return String.format(
            "Номер последней сделки: %s, исторические данные выгружены на %s.",
            lastTradingNumber,
            lastHistoryDate
        );
    }

    public void updateLastTradingNumber(List<IntradayValue> intraday) {
        intraday
            .stream()
            .mapToLong(IntradayValue::getNumber)
            .max()
            .ifPresent(this::setLastTradingNumber);
    }

    public void updateLastHistoryDate(List<HistoryValue> history) {
        history
            .stream()
            .max(HistoryValue::compareTo)
            .map(HistoryValue::getTradeDate)
            .ifPresent(this::setLastHistoryDate);
    }

    private void setLastHistoryDate(LocalDate lastHistoryDate) {
        this.lastHistoryDate = lastHistoryDate;
    }

    private void setLastTradingNumber(Long lastTradingNumber) {
        this.lastTradingNumber = lastTradingNumber;
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
}
