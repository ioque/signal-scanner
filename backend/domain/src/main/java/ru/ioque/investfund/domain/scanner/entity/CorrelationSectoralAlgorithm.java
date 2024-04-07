package ru.ioque.investfund.domain.scanner.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
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
public class CorrelationSectoralAlgorithm extends ScannerAlgorithm {
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
    public ScanningResult run(UUID scannerId, List<TradingSnapshot> tradingSnapshots, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ScannerLog> logs = new ArrayList<>();
        boolean futuresIsRiseOvernight = getFuturesStatistic(tradingSnapshots).isRiseOvernight(futuresOvernightScale);
        logs.add(runWorkMessage(futuresIsRiseOvernight));
        for (var finInstrument : analyzeInstruments(tradingSnapshots)) {
            logs.add(parametersMessage(finInstrument));
            if (futuresIsRiseOvernight && finInstrument.isRiseOvernight(stockOvernightScale)) {
                signals.add(new Signal(dateTimeNow, finInstrument.getTicker(), true));
            }
        }
        logs.add(finishWorkMessage(signals));
        return ScanningResult.builder()
            .dateTime(dateTimeNow)
            .signals(signals)
            .logs(logs)
            .build();
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

    private ScannerLog parametersMessage(
        TradingSnapshot tradingSnapshot
    ) {
        return new ScannerLog(
            String
                .format(
                    "Инструмент %s. ",
                    tradingSnapshot.getTicker()
                )
                .concat(tradingSnapshot.isRiseOvernight(stockOvernightScale) ? "Инструмент рос в предыдущие два дня."
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
