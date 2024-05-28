package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;

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
    public List<Signal> findSignals(List<InstrumentPerformance> instruments, LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        final List<InstrumentPerformance> riseInstruments = getRiseInstruments(instruments);
        final List<InstrumentPerformance> otherInstruments = getSectoralRetards(instruments, riseInstruments);
        if (!otherInstruments.isEmpty() && Math.round((double) riseInstruments.size() / instruments.size() * 100) >= 70) {
            otherInstruments.forEach(snapshot -> {
                signals.add(
                    Signal.builder()
                        .instrumentId(snapshot.getInstrumentId())
                        .isBuy(true)
                        .summary(String.format(
                            """
                            Растущие инструменты сектора: %s
                            Падающие инструменты сектора: %s
                            """,
                            riseInstruments.stream().map(InstrumentPerformance::getTicker).toList(),
                            otherInstruments.stream().map(InstrumentPerformance::getTicker).toList()
                        ))
                        .watermark(watermark)
                        .price(snapshot.getIntradayPerformance().map(IntradayPerformance::getTodayLastPrice).orElse(0D))
                        .build()
                );
            });
        }
        return signals;
    }

    private List<InstrumentPerformance> getSectoralRetards(
        List<InstrumentPerformance> tradingSnapshots,
        List<InstrumentPerformance> riseInstruments
    ) {
        return tradingSnapshots
            .stream()
            .filter(row -> !riseInstruments.contains(row))
            .toList();
    }

    private List<InstrumentPerformance> getRiseInstruments(List<InstrumentPerformance> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(row -> row.isRiseInLastTwoDay(historyScale, intradayScale))
            .toList();
    }
}
