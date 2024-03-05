package ru.ioque.investfund.domain.scanner.financial.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;
import ru.ioque.investfund.domain.scanner.financial.entity.Report;
import ru.ioque.investfund.domain.scanner.financial.entity.ScannerLog;
import ru.ioque.investfund.domain.scanner.financial.entity.Signal;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalAlgorithm;

import java.time.Instant;
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
    public Report run(UUID scannerId, List<InstrumentStatistic> statistics, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ScannerLog> logs = new ArrayList<>();
        logs.add(runWorkMessage());
        List<InstrumentStatistic> riseInstruments = getRiseInstruments(statistics);
        List<InstrumentStatistic> otherInstruments = getSectoralRetards(statistics, riseInstruments);
        logs.add(parametersMessage(riseInstruments, otherInstruments));
        if (!otherInstruments.isEmpty() && Math.round((double) riseInstruments.size() / statistics.size() * 100) >= 70) {
            otherInstruments.forEach(row -> signals.add(new Signal(dateTimeNow, row.getInstrumentId(), true)));
        }
        logs.add(finishWorkMessage(signals));
        return Report
            .builder()
            .scannerId(scannerId)
            .time(dateTimeNow)
            .signals(signals)
            .logs(logs)
            .build();
    }

    private static List<InstrumentStatistic> getSectoralRetards(
        List<InstrumentStatistic> statistics,
        List<InstrumentStatistic> riseInstruments
    ) {
        return statistics
            .stream()
            .filter(row -> !riseInstruments.contains(row))
            .toList();
    }

    private List<InstrumentStatistic> getRiseInstruments(List<InstrumentStatistic> statistics) {
        return statistics
            .stream()
            .filter(row -> row.isRiseInLastTwoDay(historyScale, intradayScale))
            .toList();
    }

    private ScannerLog parametersMessage(List<InstrumentStatistic> riseInstruments, List<InstrumentStatistic> otherInstruments) {
        return new ScannerLog(
            String.format(
                "Растущие инструменты сектора: %s, секторальные отстающий(е): %s.",
                riseInstruments.stream().map(InstrumentStatistic::getTicker).toList(),
                otherInstruments.stream().map(InstrumentStatistic::getTicker).toList()
            ),
            Instant.now()
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
            Instant.now()
        );
    }

    private ScannerLog finishWorkMessage(List<Signal> signals) {
        return new ScannerLog(
            String.format(
                "Завершена обработка данных по алгоритму %s. Количество сигналов: %s.",
                getName(),
                signals.size()
            ),
            Instant.now()
        );
    }
}
