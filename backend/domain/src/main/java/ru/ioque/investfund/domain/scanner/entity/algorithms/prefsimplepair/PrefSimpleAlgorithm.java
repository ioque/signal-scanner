package ru.ioque.investfund.domain.scanner.entity.algorithms.prefsimplepair;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.value.PrefSimplePair;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.ScannerLog;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class PrefSimpleAlgorithm extends ScannerAlgorithm {
    private final Double spreadParam;

    public PrefSimpleAlgorithm(Double spreadParam) {
        super("Дельта анализ цен пар преф-обычка");
        this.spreadParam = spreadParam;
        new PrefSimpleAlgorithmValidator(this);
    }

    @Override
    public ScanningResult run(UUID scannerId, List<TradingSnapshot> tradingSnapshots, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ScannerLog> logs = new ArrayList<>();
        logs.add(runWorkMessage());
        findAllPrefAndSimplePairs(tradingSnapshots).forEach(pair -> {
            final double currentDelta = pair.getCurrentDelta();
            final double historyDelta = pair.getHistoryDelta();
            final double multiplier = currentDelta / historyDelta;
            logs.add(parametersMessage(pair, currentDelta, historyDelta, multiplier));
            if (multiplier > spreadParam) {
                signals.add(new Signal(dateTimeNow, pair.getPref().getTicker(), true));
            }
        });
        logs.add(finishWorkMessage(signals));
        return ScanningResult.builder()
            .dateTime(dateTimeNow)
            .signals(signals)
            .logs(logs)
            .build();
    }

    private List<PrefSimplePair> findAllPrefAndSimplePairs(List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(TradingSnapshot::isPref)
            .map(pref -> new PrefSimplePair(pref, findSimplePair(tradingSnapshots, pref)))
            .toList();
    }

    private TradingSnapshot findSimplePair(
        List<TradingSnapshot> tradingSnapshots,
        TradingSnapshot tradingSnapshot
    ) {
        return tradingSnapshots
            .stream()
            .filter(tradingSnapshot::isSimplePair)
            .findFirst()
            .orElseThrow(() -> new DomainException("Для привилегированной акции " + tradingSnapshot.getTicker() + " не найдена обычная акция."));
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
