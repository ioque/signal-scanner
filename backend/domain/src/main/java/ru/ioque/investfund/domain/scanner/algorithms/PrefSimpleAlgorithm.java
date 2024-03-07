package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.entity.ScanningResult;
import ru.ioque.investfund.domain.scanner.entity.PrefSimplePair;
import ru.ioque.investfund.domain.scanner.entity.ScannerLog;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalAlgorithm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class PrefSimpleAlgorithm extends SignalAlgorithm {
    private final Double spreadParam;

    public PrefSimpleAlgorithm(Double spreadParam) {
        super("Дельта анализ цен пар преф-обычка");
        this.spreadParam = spreadParam;
    }

    @Override
    public ScanningResult run(UUID scannerId, List<FinInstrument> finInstruments, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ScannerLog> logs = new ArrayList<>();
        logs.add(runWorkMessage());
        findAllPrefAndSimplePairs(finInstruments).forEach(pair -> {
            final double currentDelta = pair.getCurrentDelta();
            final double historyDelta = pair.getHistoryDelta();
            final double multiplier = currentDelta / historyDelta;
            logs.add(parametersMessage(pair, currentDelta, historyDelta, multiplier));
            if (multiplier > spreadParam) {
                signals.add(new Signal(dateTimeNow, pair.getPref().getInstrumentId(), true));
            }
        });
        logs.add(finishWorkMessage(signals));
        return ScanningResult.builder()
            .dateTime(dateTimeNow)
            .signals(signals)
            .logs(logs)
            .build();
    }

    private List<PrefSimplePair> findAllPrefAndSimplePairs(List<FinInstrument> finInstruments) {
        return finInstruments
            .stream()
            .filter(FinInstrument::isPref)
            .map(pref -> new PrefSimplePair(pref, findSimplePair(finInstruments, pref)))
            .toList();
    }

    private FinInstrument findSimplePair(
        List<FinInstrument> finInstruments,
        FinInstrument finInstrument
    ) {
        return finInstruments
            .stream()
            .filter(finInstrument::isSimplePair)
            .findFirst()
            .orElseThrow(() -> new DomainException("Для привилегированной акции " + finInstrument.getTicker() + " не найдена обычная акция."));
    }

    private ScannerLog runWorkMessage() {
        return new ScannerLog(
            String
                .format(
                    "Начата обработка данных по алгоритму %s. Параметр spreadParam = %s.",
                    getName(),
                    spreadParam
                ),
            LocalDateTime.now()
        );
    }

    private ScannerLog parametersMessage(
        PrefSimplePair prefSimplePair,
        double currentDelta,
        double historyDelta,
        double multiplier
    ) {
        return new ScannerLog(
            String.format(
                "Пара преф-обычка %s-%s, текущая дельта внутри дня: %s, историческая дельта: %s, отношение текущей дельты к исторической: %s.",
                prefSimplePair.getPref().getTicker(),
                prefSimplePair.getSimple().getTicker(),
                currentDelta,
                historyDelta,
                multiplier
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
