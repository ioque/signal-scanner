package ru.ioque.investfund.domain.scanner.algorithms.impl;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.algorithms.core.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.value.InstrumentTradingState;
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
    public List<Signal> findSignals(List<InstrumentTradingState> instruments, LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        final List<InstrumentTradingState> riseInstruments = getRiseInstruments(instruments);
        final List<InstrumentTradingState> otherInstruments = getSectoralRetards(instruments, riseInstruments);
        if (!otherInstruments.isEmpty() && Math.round((double) riseInstruments.size() / instruments.size() * 100) >= 70) {
            otherInstruments.forEach(snapshot -> signals.add(
                Signal.builder()
                    .instrumentId(snapshot.getInstrumentId())
                    .isBuy(true)
                    .summary(String.format(
                        """
                        Растущие инструменты сектора: %s
                        Падающие инструменты сектора: %s
                        """,
                        riseInstruments.stream().map(InstrumentTradingState::getTicker).toList(),
                        otherInstruments.stream().map(InstrumentTradingState::getTicker).toList()
                    ))
                    .watermark(watermark)
                    .price(snapshot.getIntradayPerformance().getTodayLastPrice())
                    .build()
            ));
        }
        return signals;
    }

    private List<InstrumentTradingState> getSectoralRetards(
        List<InstrumentTradingState> tradingSnapshots,
        List<InstrumentTradingState> riseInstruments
    ) {
        return tradingSnapshots
            .stream()
            .filter(row -> !riseInstruments.contains(row))
            .toList();
    }

    private List<InstrumentTradingState> getRiseInstruments(List<InstrumentTradingState> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(row -> row.isRiseInLastTwoDay(historyScale, intradayScale))
            .toList();
    }
}
