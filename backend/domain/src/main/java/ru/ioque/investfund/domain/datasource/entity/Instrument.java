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
import ru.ioque.investfund.domain.datasource.value.AggregateHistory;
import ru.ioque.investfund.domain.datasource.value.TradingState;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;
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
    final TreeSet<AggregateHistory> aggregateHistory;

    @Builder
    public Instrument(
        InstrumentId id,
        InstrumentDetails details,
        Boolean updatable,
        Long lastTradingNumber,
        LocalDate lastHistoryDate,
        TradingState tradingState,
        TreeSet<AggregateHistory> aggregateHistory
    ) {
        super(id);
        this.details = details;
        this.updatable = updatable;
        this.tradingState = tradingState;
        this.aggregateHistory = aggregateHistory;
    }

    public static Instrument of(InstrumentId id, InstrumentDetails details) {
        return Instrument.builder()
            .id(id)
            .details(details)
            .updatable(false)
            .aggregateHistory(new TreeSet<>())
            .build();
    }

    public void updateDetails(InstrumentDetails details) {
        this.details = details;
    }

    public void updateTradingState(TreeSet<IntradayValue> intradayValues) {
        if (intradayValues.isEmpty()) {
            return;
        }
        if (tradingState == null) {
            tradingState = TradingState.from(intradayValues);
            addEvent(UpdateTradingStateEvent.of(getId(), tradingState));
            return;
        }
        tradingState = TradingState.of(tradingState, intradayValues);
        addEvent(UpdateTradingStateEvent.of(getId(), tradingState));
    }

    public void updateAggregateHistory(TreeSet<HistoryValue> historyValues) {
        if (historyValues.isEmpty()) {
            return;
        }
        if (aggregateHistory.isEmpty()) {
            aggregateHistory.addAll(
                historyValues.stream().map(AggregateHistory::from).toList()
            );
            return;
        }
        Optional<LocalDate> lastHistoryDate = getLastHistoryDate();
        aggregateHistory.addAll(
            historyValues
                .stream()
                .filter(historyValue -> historyValue.getTradeDate().isAfter(lastHistoryDate.get()))
                .map(AggregateHistory::from)
                .toList()
        );
    }

    public Ticker getTicker() {
        return details.getTicker();
    }

    public InstrumentType getType() {
        return details.getType();
    }

    public Optional<LocalDate> getLastHistoryDate() {
        return Optional.ofNullable(aggregateHistory.last()).map(AggregateHistory::getDate);
    }

    public LocalDate historyRightBound(LocalDate now) {
        return now.minusDays(1);
    }

    public LocalDate historyLeftBound(LocalDate now) {
        return getLastHistoryDate().map(lastHistoryDate -> lastHistoryDate.plusDays(1)).orElse(now.minusMonths(6));
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
