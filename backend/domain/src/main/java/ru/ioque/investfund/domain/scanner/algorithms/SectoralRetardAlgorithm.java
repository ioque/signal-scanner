package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.TradingState;
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
    public List<Signal> findSignals(List<Instrument> instruments, LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        final List<Instrument> riseInstruments = getRiseInstruments(instruments);
        final List<Instrument> otherInstruments = getSectoralRetards(instruments, riseInstruments);
        if (!otherInstruments.isEmpty() && Math.round((double) riseInstruments.size() / instruments.size() * 100) >= 70) {
            otherInstruments.forEach(snapshot -> {
                signals.add(
                    Signal.builder()
                        .instrumentId(snapshot.getId())
                        .isBuy(true)
                        .summary(String.format(
                            """
                            Растущие инструменты сектора: %s
                            Падающие инструменты сектора: %s
                            """,
                            riseInstruments.stream().map(Instrument::getTicker).toList(),
                            otherInstruments.stream().map(Instrument::getTicker).toList()
                        ))
                        .watermark(watermark)
                        .price(snapshot.getTradingState().map(TradingState::getTodayLastPrice).orElse(0D))
                        .build()
                );
            });
        }
        return signals;
    }

    private List<Instrument> getSectoralRetards(
        List<Instrument> tradingSnapshots,
        List<Instrument> riseInstruments
    ) {
        return tradingSnapshots
            .stream()
            .filter(row -> !riseInstruments.contains(row))
            .toList();
    }

    private List<Instrument> getRiseInstruments(List<Instrument> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(row -> row.isRiseInLastTwoDay(historyScale, intradayScale))
            .toList();
    }
}
