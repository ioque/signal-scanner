package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
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
    final Ticker ticker;
    final InstrumentType type;
    TradingState tradingState;
    final TreeSet<AggregatedHistory> aggregateHistories;

    @Builder
    public Instrument(
        InstrumentId id,
        Ticker ticker,
        Boolean updatable,
        InstrumentType type,
        InstrumentDetails details,
        TradingState tradingState,
        TreeSet<AggregatedHistory> aggregateHistories
    ) {
        super(id);
        this.type = type;
        this.ticker = ticker;
        this.updatable = updatable;
        this.details = details;
        this.tradingState = tradingState;
        this.aggregateHistories = aggregateHistories == null ? new TreeSet<>() : aggregateHistories;
    }

    public void updateDetails(InstrumentDetails details) {
        this.details = details;
    }

    public boolean updateTradingState(TreeSet<IntradayData> intradayData) {
        if (intradayData.isEmpty()) {
            return false;
        }
        if (tradingState == null) {
            tradingState = TradingState.from(intradayData);
            return true;
        }
        IntradayData lastIntradayData = intradayData.last();
        if (lastIntradayData.getNumber() > tradingState.getLastIntradayNumber()) {
            tradingState = TradingState.of(tradingState, intradayData);
            return true;
        }
        return false;
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

    public Optional<TradingState> getTradingState() {
        return Optional.ofNullable(tradingState);
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
        return getTradingState().map(TradingState::getLastIntradayNumber).orElse(0L);
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

    private LocalDate getLastHistoryDate() {
        return aggregateHistories.last().getDate();
    }
}
