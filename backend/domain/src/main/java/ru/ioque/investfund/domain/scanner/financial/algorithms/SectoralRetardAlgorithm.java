package ru.ioque.investfund.domain.scanner.financial.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.scanner.financial.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.financial.entity.ScannerLog;
import ru.ioque.investfund.domain.scanner.financial.entity.ScanningResult;
import ru.ioque.investfund.domain.scanner.financial.entity.Signal;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalAlgorithm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SectoralRetardAlgorithm extends SignalAlgorithm {
    public final String name = "Секторальный отстающий";
    private final Double historyScale;
    private final Double intradayScale;

    public SectoralRetardAlgorithm(Double historyScale, Double intradayScale) {
        super("Секторальный отстающий");
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    @Override
    public ScanningResult run(UUID scannerId, List<FinInstrument> finInstruments, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ScannerLog> logs = new ArrayList<>();
        logs.add(runWorkMessage());
        List<FinInstrument> riseInstruments = getRiseInstruments(finInstruments);
        List<FinInstrument> otherInstruments = getSectoralRetards(finInstruments, riseInstruments);
        logs.add(parametersMessage(riseInstruments, otherInstruments));
        if (!otherInstruments.isEmpty() && Math.round((double) riseInstruments.size() / finInstruments.size() * 100) >= 70) {
            otherInstruments.forEach(row -> signals.add(new Signal(dateTimeNow, row.getInstrumentId(), true)));
        }
        logs.add(finishWorkMessage(signals));
        return ScanningResult
            .builder()
            .signals(signals)
            .logs(logs)
            .build();
    }

    private static List<FinInstrument> getSectoralRetards(
        List<FinInstrument> finInstruments,
        List<FinInstrument> riseInstruments
    ) {
        return finInstruments
            .stream()
            .filter(row -> !riseInstruments.contains(row))
            .toList();
    }

    private List<FinInstrument> getRiseInstruments(List<FinInstrument> finInstruments) {
        return finInstruments
            .stream()
            .filter(row -> row.isRiseInLastTwoDay(historyScale, intradayScale))
            .toList();
    }

    private ScannerLog parametersMessage(List<FinInstrument> riseInstruments, List<FinInstrument> otherInstruments) {
        return new ScannerLog(
            String.format(
                "Растущие инструменты сектора: %s, секторальные отстающий(е): %s.",
                riseInstruments.stream().map(FinInstrument::getTicker).toList(),
                otherInstruments.stream().map(FinInstrument::getTicker).toList()
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
