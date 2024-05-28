package ru.ioque.investfund.domain.scanner.value;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@Builder
@AllArgsConstructor
public class InstrumentPerformance {
    private final Ticker ticker;
    private final InstrumentId instrumentId;
    private IntradayPerformance intradayPerformance;
    private List<AggregatedHistory> aggregatedHistories;

    public Optional<IntradayPerformance> getIntradayPerformance() {
        return Optional.ofNullable(intradayPerformance);
    }

    public void updatePerformance(IntradayPerformance intradayPerformance) {
        this.intradayPerformance = intradayPerformance;
    }

    public Optional<Double> getHistoryMedianValue(int period) {
        if (aggregatedHistories.isEmpty()) return Optional.empty();
        if (aggregatedHistories.size() < period) return Optional.empty();
        var sortedValues = aggregatedHistories.stream().mapToDouble(AggregatedHistory::getValue).sorted().toArray();
        var n = sortedValues.length;
        if (n % 2 != 0) {
            return Optional.of(sortedValues[(n / 2)]);
        }
        return Optional.of((sortedValues[(n - 1) / 2] + sortedValues[(n / 2)]) / 2.0);
    }

    public Optional<Boolean> isRiseToday() {
        if (intradayPerformance == null ||
            intradayPerformance.getTodayLastPrice() == null ||
            intradayPerformance.getTodayFirstPrice() == null) {
            return Optional.empty();
        }
        return getPrevClosePrice()
            .map(prevClosePrice -> intradayPerformance.getTodayLastPrice() > prevClosePrice &&
                intradayPerformance.getTodayLastPrice() > intradayPerformance.getTodayFirstPrice());
    }

    public boolean isRiseOvernight(double scale) {
        if (intradayPerformance == null || intradayPerformance.getTodayLastPrice() == null) {
            return false;
        }
        return getPrevClosePrice()
            .filter(prevClosePrice -> ((intradayPerformance.getTodayLastPrice() / prevClosePrice) - 1) > scale)
            .isPresent();
    }

    public boolean isRiseForPrevDay(double scale) {
        var prevClosePrice = getPrevClosePrice();
        var prevPrevClosePrice = getPrevPrevClosePrice();
        if (prevPrevClosePrice.isEmpty() || prevClosePrice.isEmpty()) return false;
        return ((prevClosePrice.get() / prevPrevClosePrice.get()) - 1) > scale;
    }

    public boolean isRiseForToday(double scale) {
        if (intradayPerformance == null ||
            intradayPerformance.getTodayLastPrice() == null ||
            intradayPerformance.getTodayFirstPrice() == null) {
            return false;
        }
        return ((intradayPerformance.getTodayLastPrice()  / intradayPerformance.getTodayFirstPrice() ) - 1) > scale;
    }

    public boolean isRiseInLastTwoDay(double historyScale, double intradayScale) {
        return isRiseForPrevDay(historyScale) && isRiseForToday(intradayScale);
    }

    public Optional<Double> getPrevPrevClosePrice() {
        final Optional<LocalDate> lastTradingDate = aggregatedHistories
            .stream()
            .max(AggregatedHistory::compareTo)
            .map(AggregatedHistory::getDate)
            .map(LocalDate.class::cast);
        if (lastTradingDate.isEmpty()) return Optional.empty();
        final LocalDate prevLastTradingDate = getPrevTradingDate(lastTradingDate.get());
        return aggregatedHistories.stream()
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
        return aggregatedHistories
            .stream()
            .max(AggregatedHistory::compareTo)
            .stream()
            .findFirst()
            .map(AggregatedHistory::getClosePrice);
    }

    public boolean isPref() {
        return getTicker().getValue().length() == 5;
    }

    public boolean isSimplePair(InstrumentPerformance instrument) {
        return getTicker()
            .getValue()
            .substring(0, getTicker().getValue().length() - 1)
            .equals(instrument.getTicker().getValue());
    }
}
