package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.event.UpdateTradingStateEvent;
import ru.ioque.investfund.domain.datasource.value.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.TradingState;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDate;
import java.util.Optional;
import java.util.TreeSet;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Instrument extends Domain<InstrumentId> {
    InstrumentDetails details;
    Boolean updatable;
    TradingState tradingState;
    final TreeSet<AggregatedHistory> aggregateHistories;

    @Builder
    public Instrument(
        InstrumentId id,
        InstrumentDetails details,
        Boolean updatable,
        Long lastTradingNumber,
        LocalDate lastHistoryDate,
        TradingState tradingState,
        TreeSet<AggregatedHistory> aggregateHistories
    ) {
        super(id);
        this.details = details;
        this.updatable = updatable;
        this.tradingState = tradingState;
        this.aggregateHistories = aggregateHistories;
    }

    public static Instrument of(InstrumentId id, InstrumentDetails details) {
        return Instrument.builder()
            .id(id)
            .details(details)
            .updatable(false)
            .aggregateHistories(new TreeSet<>())
            .build();
    }

    public void updateDetails(InstrumentDetails details) {
        this.details = details;
    }

    public void updateTradingState(TreeSet<IntradayData> intradayData) {
        if (intradayData.isEmpty()) {
            return;
        }
        if (tradingState == null) {
            tradingState = TradingState.from(intradayData);
            addEvent(UpdateTradingStateEvent.of(getId(), tradingState));
            return;
        }
        IntradayData lastIntradayData = intradayData.last();
        if (lastIntradayData.getNumber() > tradingState.getLastNumber()) {
            tradingState = TradingState.of(tradingState, intradayData);
            addEvent(UpdateTradingStateEvent.of(getId(), tradingState));
        }
    }

    public void updateAggregateHistory(TreeSet<AggregatedHistory> aggregateHistories) {
        if (aggregateHistories.isEmpty()) {
            return;
        }
        if (this.aggregateHistories.isEmpty()) {
            this.aggregateHistories.addAll(aggregateHistories);
            return;
        }
        LocalDate lastHistoryDate = getLastHistoryDate();
        this.aggregateHistories.addAll(
            aggregateHistories
                .stream()
                .filter(history -> history.getDate().isAfter(lastHistoryDate))
                .toList()
        );
    }

    public Ticker getTicker() {
        return details.getTicker();
    }

    public InstrumentType getType() {
        return details.getType();
    }

    public LocalDate getLastHistoryDate() {
        return aggregateHistories.last().getDate();
    }

    public LocalDate historyRightBound(LocalDate now) {
        return now.minusDays(1);
    }

    public LocalDate historyLeftBound(LocalDate now) {
        if (aggregateHistories.isEmpty()) {
            return now.minusMonths(6);
        }
        return getLastHistoryDate().plusDays(1);
    }

    public Long getLastTradingNumber() {
        return Optional.ofNullable(tradingState).map(TradingState::getLastNumber).orElse(0L);
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
}
