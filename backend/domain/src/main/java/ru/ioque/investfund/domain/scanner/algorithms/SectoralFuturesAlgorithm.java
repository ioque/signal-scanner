package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralFuturesAlgorithm extends ScannerAlgorithm {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;

    public SectoralFuturesAlgorithm(
        SectoralFuturesProperties properties
    ) {
        super(properties.getType().getName());
        setFuturesOvernightScale(properties.getFuturesOvernightScale());
        setStockOvernightScale(properties.getStockOvernightScale());
        setFuturesTicker(properties.getFuturesTicker());
    }

    @Override
    public List<Signal> run(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        final boolean futuresIsRiseOvernight = getFuturesStatistic(tradingSnapshots).isRiseOvernight(futuresOvernightScale);
        for (final TradingSnapshot snapshot : analyzeInstruments(tradingSnapshots)) {
            final boolean riseOvernight = snapshot.isRiseOvernight(stockOvernightScale);
            final String summary = String.format(
                "тренд инструмента %s; тренд фьючерса %s;",
                (riseOvernight ? "растущий" : "нисходящий"),
                (futuresIsRiseOvernight ? "растущий" : "нисходящий")
            );
            if (futuresIsRiseOvernight && riseOvernight) {
                signals.add(
                    Signal.builder()
                        .isOpen(true)
                        .isBuy(true)
                        .summary(summary)
                        .dateTime(watermark)
                        .ticker(snapshot.getTicker())
                        .price(snapshot.getTodayLastPrice().orElse(0D))
                        .build()
                );
            }
        }
        return signals;
    }

    private TradingSnapshot getFuturesStatistic(List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(row -> futuresTicker.equals(row.getTicker()))
            .findFirst()
            .orElseThrow(() -> new DomainException("Не добавлен фьючерс на основной товар сектора."));
    }

    private List<TradingSnapshot> analyzeInstruments(List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots.stream().filter(row -> !row.getTicker().equals(futuresTicker)).toList();
    }

    private void setFuturesOvernightScale(Double futuresOvernightScale) {
        if (futuresOvernightScale == null) {
            throw new DomainException("Не передан параметр futuresOvernightScale.");
        }
        if (futuresOvernightScale <= 0) {
            throw new DomainException("Параметр futuresOvernightScale должен быть больше нуля.");
        }
        this.futuresOvernightScale = futuresOvernightScale;
    }

    private void setStockOvernightScale(Double stockOvernightScale) {
        if (stockOvernightScale == null) {
            throw new DomainException("Не передан параметр stockOvernightScale.");
        }
        if (stockOvernightScale <= 0) {
            throw new DomainException("Параметр stockOvernightScale должен быть больше нуля.");
        }
        this.stockOvernightScale = stockOvernightScale;
    }

    private void setFuturesTicker(String futuresTicker) {
        if (futuresTicker == null || futuresTicker.isEmpty()) {
            throw new DomainException("Не передан параметр futuresTicker.");
        }
        this.futuresTicker = futuresTicker;
    }
}
