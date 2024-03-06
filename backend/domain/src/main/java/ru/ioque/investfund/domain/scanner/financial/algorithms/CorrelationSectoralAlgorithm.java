package ru.ioque.investfund.domain.scanner.financial.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.DomainException;
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
public class CorrelationSectoralAlgorithm extends SignalAlgorithm {
    private final Double futuresOvernightScale;
    private final Double stockOvernightScale;
    private final String futuresTicker;

    public CorrelationSectoralAlgorithm(
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super("Корреляция сектора с фьючерсом на основной товар сектора");
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    @Override
    public ScanningResult run(UUID scannerId, List<FinInstrument> finInstruments, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ScannerLog> logs = new ArrayList<>();
        boolean futuresIsRiseOvernight = getFuturesStatistic(finInstruments).isRiseOvernight(futuresOvernightScale);
        logs.add(runWorkMessage(futuresIsRiseOvernight));
        for (var statistic : analyzeInstruments(finInstruments)) {
            logs.add(parametersMessage(statistic));
            if (futuresIsRiseOvernight && statistic.isRiseOvernight(stockOvernightScale)) {
                signals.add(new Signal(dateTimeNow, statistic.getInstrumentId(), true));
            }
        }
        logs.add(finishWorkMessage(signals));
        return ScanningResult.builder()
            .signals(signals)
            .logs(logs)
            .build();
    }

    private FinInstrument getFuturesStatistic(List<FinInstrument> finInstruments) {
        return finInstruments
            .stream()
            .filter(row -> futuresTicker.equals(row.getTicker()))
            .findFirst()
            .orElseThrow(() -> new DomainException("Не добавлен фьючерс на основной товар сектора."));
    }

    private List<FinInstrument> analyzeInstruments(List<FinInstrument> finInstruments) {
        return finInstruments.stream().filter(row -> !row.getTicker().equals(futuresTicker)).toList();
    }

    private ScannerLog parametersMessage(
        FinInstrument finInstrument
    ) {
        return new ScannerLog(
            String
                .format(
                    "Инструмент %s. ",
                    finInstrument.getTicker()
                )
                .concat(finInstrument.isRiseOvernight(stockOvernightScale) ? "Инструмент рос в предыдущие два дня."
                    : "Инструмент не рос в предыдущие два дня."),
            LocalDateTime.now()
        );
    }

    private ScannerLog runWorkMessage(boolean futuresIsRiseToday) {
        return new ScannerLog(
            String
                .format(
                    "Начата обработка данных по алгоритму %s. Параметр futuresOvernightScale = %s, параметр stockOvernightScale = %s, в качестве фьючерса сектора выбран %s. ",
                    getName(),
                    futuresOvernightScale,
                    stockOvernightScale,
                    futuresTicker
                )
                .concat(futuresIsRiseToday ? "Фьючерс рос в предыдущий день." : "Фьючерс не рос в предыдущий день."),
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
