package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<Signal> run(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        final List<TradingSnapshot> riseInstruments = getRiseInstruments(tradingSnapshots);
        final List<TradingSnapshot> otherInstruments = getSectoralRetards(tradingSnapshots, riseInstruments);
        if (!otherInstruments.isEmpty() && Math.round((double) riseInstruments.size() / tradingSnapshots.size() * 100) >= 70) {
            otherInstruments.forEach(snapshot -> {
                final String summary = String.format(
                    "Растущие инструменты сектора: %s",
                    riseInstruments.stream().map(TradingSnapshot::getTicker).toList()
                );
                signals.add(
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
        return signals;
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
