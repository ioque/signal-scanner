package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.IntradayStatistic;
import ru.ioque.investfund.domain.datasource.value.TradingState;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;
import java.util.TreeSet;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Instrument extends Domain<InstrumentId> {
    InstrumentDetail detail;
    Boolean updatable;
    TradingState tradingState;
    final TreeSet<AggregatedHistory> aggregateHistories;

    @Builder
    public Instrument(
        InstrumentId id,
        Boolean updatable,
        InstrumentDetail detail,
        TradingState tradingState,
        TreeSet<AggregatedHistory> aggregateHistories
    ) {
        super(id);
        this.updatable = updatable;
        this.detail = detail;
        this.tradingState = tradingState;
        this.aggregateHistories = aggregateHistories == null ? new TreeSet<>() : aggregateHistories;
    }

    public Ticker getTicker() {
        return detail.getTicker();
    }

    public InstrumentType getType() {
        return detail.getType();
    }

    public void updateDetails(InstrumentDetail details) {
        this.detail = details;
    }

    public void updatePerformance(IntradayStatistic intradayStatistic) {

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

    public LocalDate getLastHistoryDate() {
        return aggregateHistories.last().getDate();
    }

    public boolean contains(AggregatedHistory data) {
        return aggregateHistories.contains(data);
    }

    public Optional<Double> getHistoryMedianValue(int period) {
        if (aggregateHistories.isEmpty()) return Optional.empty();
        if (aggregateHistories.size() < period) return Optional.empty();
        var sortedValues = aggregateHistories.stream().mapToDouble(AggregatedHistory::getValue).sorted().toArray();
        var n = sortedValues.length;
        if (n % 2 != 0) {
            return Optional.of(sortedValues[(n / 2)]);
        }
        return Optional.of((sortedValues[(n - 1) / 2] + sortedValues[(n / 2)]) / 2.0);
    }

    public Optional<Boolean> isRiseToday() {
        if (tradingState == null ||
            tradingState.getTodayLastPrice() == null ||
            tradingState.getTodayFirstPrice() == null) {
            return Optional.empty();
        }
        return getPrevClosePrice()
            .map(prevClosePrice -> tradingState.getTodayLastPrice() > prevClosePrice &&
                tradingState.getTodayLastPrice() > tradingState.getTodayFirstPrice());
    }

    public boolean isRiseOvernight(double scale) {
        if (tradingState == null || tradingState.getTodayLastPrice() == null) {
            return false;
        }
        return getPrevClosePrice()
            .filter(prevClosePrice -> ((tradingState.getTodayLastPrice() / prevClosePrice) - 1) > scale)
            .isPresent();
    }

    public boolean isRiseForPrevDay(double scale) {
        var prevClosePrice = getPrevClosePrice();
        var prevPrevClosePrice = getPrevPrevClosePrice();
        if (prevPrevClosePrice.isEmpty() || prevClosePrice.isEmpty()) return false;
        return ((prevClosePrice.get() / prevPrevClosePrice.get()) - 1) > scale;
    }

    public boolean isRiseForToday(double scale) {
        if (tradingState == null ||
            tradingState.getTodayLastPrice() == null ||
            tradingState.getTodayFirstPrice() == null) {
            return false;
        }
        return ((tradingState.getTodayLastPrice()  / tradingState.getTodayFirstPrice() ) - 1) > scale;
    }

    public boolean isRiseInLastTwoDay(double historyScale, double intradayScale) {
        return isRiseForPrevDay(historyScale) && isRiseForToday(intradayScale);
    }

    public Optional<Double> getPrevPrevClosePrice() {
        final Optional<LocalDate> lastTradingDate = aggregateHistories
            .stream()
            .max(AggregatedHistory::compareTo)
            .map(AggregatedHistory::getDate)
            .map(LocalDate.class::cast);
        if (lastTradingDate.isEmpty()) return Optional.empty();
        final LocalDate prevLastTradingDate = getPrevTradingDate(lastTradingDate.get());
        return aggregateHistories.stream()
            .filter(row -> row.getDate().equals(prevLastTradingDate))
            .findFirst()
            .map(AggregatedHistory::getClosePrice);
    }

    private LocalDate getPrevTradingDate(LocalDate tradingDate) {
        LocalDate day = tradingDate.minusDays(1);
        if (day.getDayOfWeek().equals(DayOfWeek.SUNDAY)) day = day.minusDays(2);
        if (day.getDayOfWeek().equals(DayOfWeek.SATURDAY)) day = day.minusDays(1);
        return day;
    }

    public Optional<Double> getPrevClosePrice() {
        return aggregateHistories
            .stream()
            .max(AggregatedHistory::compareTo)
            .stream()
            .findFirst()
            .map(AggregatedHistory::getClosePrice);
    }

    public boolean isPref() {
        return getTicker().getValue().length() == 5;
    }

    public boolean isSimplePair(Instrument instrument) {
        return getTicker()
            .getValue()
            .substring(0, getTicker().getValue().length() - 1)
            .equals(instrument.getTicker().getValue());
    }
}
