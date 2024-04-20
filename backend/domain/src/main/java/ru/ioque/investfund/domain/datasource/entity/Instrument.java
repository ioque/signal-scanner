package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.history.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayBatch;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Instrument extends Domain<InstrumentId> {
    InstrumentDetails details;
    Boolean updatable;
    Long lastTradingNumber;
    LocalDate lastHistoryDate;
    final List<HistoryValue> aggregateHistory;

    @Builder
    public Instrument(
        InstrumentId id,
        InstrumentDetails details,
        Boolean updatable,
        Long lastTradingNumber,
        LocalDate lastHistoryDate,
        List<HistoryValue> aggregateHistory
    ) {
        super(id);
        this.details = details;
        this.updatable = updatable;
        this.lastHistoryDate = lastHistoryDate;
        this.aggregateHistory = aggregateHistory;
        this.lastTradingNumber = lastTradingNumber;
    }

    public static Instrument of(InstrumentId id, InstrumentDetails details) {
        return Instrument.builder()
            .id(id)
            .details(details)
            .updatable(false)
            .aggregateHistory(new ArrayList<>())
            .build();
    }

    public void updateDetails(InstrumentDetails details) {
        this.details = details;
    }

    public Ticker getTicker() {
        return details.getTicker();
    }

    public InstrumentType getType() {
        return details.getType();
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
