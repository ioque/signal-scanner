package ru.ioque.investfund.domain.scanner.financial.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;
import ru.ioque.investfund.domain.scanner.financial.entity.PrefSimplePair;
import ru.ioque.investfund.domain.scanner.financial.entity.Report;
import ru.ioque.investfund.domain.scanner.financial.entity.ReportLog;
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
public class PrefSimpleAlgorithm extends SignalAlgorithm {
    private final Double spreadParam;

    public PrefSimpleAlgorithm(Double spreadParam) {
        super("Дельта анализ цен пар преф-обычка");
        this.spreadParam = spreadParam;
    }

    @Override
    public Report run(UUID scannerId, List<InstrumentStatistic> statistics, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ReportLog> logs = new ArrayList<>();
        logs.add(runWorkMessage());
        findAllPrefAndSimplePairs(statistics).forEach(pair -> {
            final double currentDelta = pair.getCurrentDelta();
            final double historyDelta = pair.getHistoryDelta();
            final double multiplier = currentDelta / historyDelta;
            logs.add(parametersMessage(pair, currentDelta, historyDelta, multiplier));
            if (multiplier > spreadParam) {
                signals.add(new Signal(pair.getPref().getInstrumentId(), true));
            }
        });
        logs.add(finishWorkMessage(signals));
        return Report.builder()
            .scannerId(scannerId)
            .time(dateTimeNow)
            .signals(signals)
            .logs(logs)
            .build();
    }

    private List<PrefSimplePair> findAllPrefAndSimplePairs(List<InstrumentStatistic> dataModels) {
        return dataModels
            .stream()
            .filter(InstrumentStatistic::isPref)
            .map(pref -> new PrefSimplePair(pref, findSimplePair(dataModels, pref)))
            .toList();
    }

    private InstrumentStatistic findSimplePair(
        List<InstrumentStatistic> dataModels,
        InstrumentStatistic statistic
    ) {
        return dataModels
            .stream()
            .filter(statistic::isSimplePair)
            .findFirst()
            .orElseThrow(() -> new DomainException("Для привилегированной акции " + statistic.getTicker() + " не найдена обычная акция."));
    }

    private ReportLog runWorkMessage() {
        return new ReportLog(
            String
                .format(
                    "Начата обработка данных по алгоритму %s. Параметр spreadParam = %s.",
                    getName(),
                    spreadParam
                ),
            Instant.now()
        );
    }

    private ReportLog parametersMessage(
        PrefSimplePair prefSimplePair,
        double currentDelta,
        double historyDelta,
        double multiplier
    ) {
        return new ReportLog(
            String.format(
                "Пара преф-обычка %s-%s, текущая дельта внутри дня: %s, историческая дельта: %s, отношение текущей дельты к исторической: %s.",
                prefSimplePair.getPref().getTicker(),
                prefSimplePair.getSimple().getTicker(),
                currentDelta,
                historyDelta,
                multiplier
            ),
            Instant.now()
        );
    }

    private ReportLog finishWorkMessage(List<Signal> signals) {
        return new ReportLog(
            String.format(
                "Завершена обработка данных по алгоритму %s. Количество сигналов: %s.",
                getName(),
                signals.size()
            ),
            Instant.now()
        );
    }
}
