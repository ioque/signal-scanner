package ru.ioque.investfund.domain.scanner.entity.algorithms.sectoralretard;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.value.ScannerLog;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SectoralRetardAlgorithm extends ScannerAlgorithm {
    public final String name = "Секторальный отстающий";
    private final Double historyScale;
    private final Double intradayScale;

    SectoralRetardAlgorithm(Double historyScale, Double intradayScale) {
        super("Секторальный отстающий");
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
        new SectoralRetardAlgorithmValidator(this);
    }

    @Override
    public ScanningResult run(UUID scannerId, List<TradingSnapshot> tradingSnapshots, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ScannerLog> logs = new ArrayList<>();
        logs.add(runWorkMessage());
        List<TradingSnapshot> riseInstruments = getRiseInstruments(tradingSnapshots);
        List<TradingSnapshot> otherInstruments = getSectoralRetards(tradingSnapshots, riseInstruments);
        logs.add(parametersMessage(riseInstruments, otherInstruments));
        if (!otherInstruments.isEmpty() && Math.round((double) riseInstruments.size() / tradingSnapshots.size() * 100) >= 70) {
            otherInstruments.forEach(row -> signals.add(new Signal(dateTimeNow, row.getTicker(), true)));
        }
        logs.add(finishWorkMessage(signals));
        return ScanningResult
            .builder()
            .dateTime(dateTimeNow)
            .signals(signals)
            .logs(logs)
            .build();
    }

    private static List<TradingSnapshot> getSectoralRetards(
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

    private ScannerLog parametersMessage(List<TradingSnapshot> riseInstruments, List<TradingSnapshot> otherInstruments) {
        return new ScannerLog(
            String.format(
                "Растущие инструменты сектора: %s, секторальные отстающий(е): %s.",
                riseInstruments.stream().map(TradingSnapshot::getTicker).toList(),
                otherInstruments.stream().map(TradingSnapshot::getTicker).toList()
            ),
            LocalDateTime.now()
        );
    }

    private ScannerLog runWorkMessage() {
        return new ScannerLog(
            String
                .format(
                    "Начата обработка данных по алгоритму %s. Параметр historyScale = %s, параметр intradayScale = %s.",
                    getName(),
                    historyScale,
                    intradayScale
                ),
            LocalDateTime.now()
        );
    }

    private ScannerLog finishWorkMessage(List<Signal> signals) {
        return new ScannerLog(
            String.format(
                "Завершена обработка данных по алгоритму %s. Количество сигналов: %s.",
                getName(),
                signals.size()
            ),
            LocalDateTime.now()
        );
    }
}
