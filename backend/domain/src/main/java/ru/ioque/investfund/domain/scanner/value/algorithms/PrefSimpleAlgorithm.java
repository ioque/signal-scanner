package ru.ioque.investfund.domain.scanner.value.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.ScannerLog;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.PrefSimplePair;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefSimpleAlgorithm extends ScannerAlgorithm {
    Double spreadParam;

    public PrefSimpleAlgorithm(Double spreadParam) {
        super("Дельта анализ цен пар преф-обычка");
        setSpreadParam(spreadParam);
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

    private void setSpreadParam(Double spreadParam) {
        if (spreadParam == null) {
            throw new DomainException("Не передан параметр spreadParam.");
        }
        if (spreadParam <= 0) {
            throw new DomainException("Параметр spreadParam должен быть больше нуля.");
        }
        this.spreadParam = spreadParam;
    }
}
