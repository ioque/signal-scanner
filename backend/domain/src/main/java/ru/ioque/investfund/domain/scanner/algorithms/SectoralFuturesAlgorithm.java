package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SectoralFuturesAlgorithm extends ScannerAlgorithm {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    Ticker futuresTicker;

    public SectoralFuturesAlgorithm(
        SectoralFuturesProperties properties
    ) {
        super(properties.getType().getName());
        this.futuresTicker = properties.getFuturesTicker();
        this.futuresOvernightScale = properties.getFuturesOvernightScale();
        this.stockOvernightScale = properties.getStockOvernightScale();
    }

    @Override
    public ScanningResult run(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        final ScanningResult scanningResult = new ScanningResult();
        final boolean futuresIsRiseOvernight = getFuturesStatistic(tradingSnapshots).isRiseOvernight(futuresOvernightScale);
        for (final TradingSnapshot snapshot : analyzeInstruments(tradingSnapshots)) {
            final boolean riseOvernight = snapshot.isRiseOvernight(stockOvernightScale);
            final String summary = String.format(
                """
                Тренд инструмента %s;
                Тренд фьючерса %s;""",
                (riseOvernight ? "растущий" : "нисходящий"),
                (futuresIsRiseOvernight ? "растущий" : "нисходящий")
            );
            scanningResult.addLog(summary);
            if (futuresIsRiseOvernight && riseOvernight) {
                scanningResult.addSignal(
                    Signal.builder()
                        .instrumentId(snapshot.getInstrumentId())
                        .isOpen(true)
                        .isBuy(true)
                        .summary(summary)
                        .watermark(watermark)
                        .price(snapshot.getLastPrice())
                        .build()
                );
            }
        }
        return scanningResult;
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
}
