package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SectoralRetardAlgorithm extends ScannerAlgorithm {
    Double historyScale;
    Double intradayScale;

    public SectoralRetardAlgorithm(SectoralRetardProperties properties) {
        super(properties.getType().getName());
        this.historyScale = properties.getHistoryScale();
        this.intradayScale = properties.getIntradayScale();
    }

    @Override
    public ScanningResult run(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        final ScanningResult scanningResult = new ScanningResult();
        final List<TradingSnapshot> riseInstruments = getRiseInstruments(tradingSnapshots);
        final List<TradingSnapshot> otherInstruments = getSectoralRetards(tradingSnapshots, riseInstruments);
        if (!otherInstruments.isEmpty() && Math.round((double) riseInstruments.size() / tradingSnapshots.size() * 100) >= 70) {
            otherInstruments.forEach(snapshot -> {
                final String summary = String.format(
                    """
                    Растущие инструменты сектора: %s
                    Падающие инструменты сектора: %s
                    """,
                    riseInstruments.stream().map(TradingSnapshot::getTicker).toList(),
                    otherInstruments.stream().map(TradingSnapshot::getTicker).toList()
                );
                scanningResult.addLog(summary);
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
            });
        }
        return scanningResult;
    }

    private List<TradingSnapshot> getSectoralRetards(
        List<TradingSnapshot> tradingSnapshots,
        List<TradingSnapshot> riseInstruments
    ) {
        return tradingSnapshots
            .stream()
            .filter(row -> !riseInstruments.contains(row))
            .toList();
    }

    private List<TradingSnapshot> getRiseInstruments(List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(row -> row.isRiseInLastTwoDay(historyScale, intradayScale))
            .toList();
    }
}
