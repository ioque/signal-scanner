package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.value.ScannerLog;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Поисковый алгоритм "Аномальные объемы". Предположительно эффективен для
 * неликвидных бумаг. Теория такова, что крупные игроки пампят бумагу, аномально наращивая объем.
 * После подъема цены и привлечения к бумаге хомяков, происходит продажа бумаги крупными игроками.
 * Крупные игроки в профите, их лоты втридорога раскупают хомяки, неся убытки.
 * <p>
 * Можно всегда увести на application уровень и реализовать более сложную логику с реал-тайм запросами.
 * Но в идеале бы все расчеты проводить с заранее загруженными данными.
 * Можно и так и так использовать. Достаточно написать на application-уровне декоратор.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class AnomalyVolumeAlgorithm extends SignalAlgorithm {
    private final Double scaleCoefficient;
    private final String indexTicker;
    private final Integer historyPeriod;

    public AnomalyVolumeAlgorithm(Double scaleCoefficient, Integer historyPeriod, String indexTicker) {
        super("Аномальные объемы");
        this.scaleCoefficient = scaleCoefficient;
        this.indexTicker = indexTicker;
        this.historyPeriod = historyPeriod;
    }

    @Override
    public ScanningResult run(UUID scannerId, final List<FinInstrument> finInstruments, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ScannerLog> logs = new ArrayList<>();
        final boolean indexIsRiseToday = getMarketIndex(finInstruments).isRiseToday();
        logs.add(runWorkMessage(indexIsRiseToday));
        for (final FinInstrument finInstrument : getAnalyzeStatistics(finInstruments)) {
            final double medianHistoryValue = finInstrument.getHistoryMedianValue();
            final double value = finInstrument.getTodayValue();
            final double multiplier = value / medianHistoryValue;
            logs.add(parametersMessage(finInstrument, value, medianHistoryValue, multiplier));
            if (multiplier > scaleCoefficient && indexIsRiseToday && finInstrument.isRiseToday() && finInstrument.getBuyToSellValueRatio() > 0.5) {
                signals.add(new Signal(dateTimeNow, finInstrument.getId(), true));
            }

            if (multiplier > scaleCoefficient && !indexIsRiseToday && !finInstrument.isRiseToday() && finInstrument.getBuyToSellValueRatio() < 0.5) {
                signals.add(new Signal(dateTimeNow, finInstrument.getId(), false));
            }
        }
        logs.add(finishWorkMessage(signals));
        return ScanningResult.builder()
            .dateTime(dateTimeNow)
            .signals(signals)
            .logs(logs)
            .build();
    }

    private List<FinInstrument> getAnalyzeStatistics(List<FinInstrument> finInstruments) {
        return finInstruments.stream().filter(row -> !row.getTicker().equals(indexTicker)).toList();
    }

    private FinInstrument getMarketIndex(final List<FinInstrument> finInstruments) {
        return finInstruments
            .stream()
            .filter(row -> row.getTicker().equals(indexTicker))
            .findFirst()
            .orElseThrow(() -> new DomainException("Не добавлен индекс рынка."));
    }

    private ScannerLog parametersMessage(
        FinInstrument finInstrument,
        double value,
        double waHistoryValue,
        double multiplier
    ) {
        return new ScannerLog(
            String.format(
                "Инструмент %s, текущий объем торгов внутри дня: %s, средневзвешенный исторический объем: %s, отношение текущего объема к историческому: %s.",
                finInstrument.getTicker(),
                value,
                waHistoryValue,
                multiplier
            ),
            LocalDateTime.now()
        );
    }

    private ScannerLog runWorkMessage(boolean indexIsRiseToday) {
        return new ScannerLog(
            String
                .format(
                    "Начата обработка данных по алгоритму %s. Параметр scaleCoefficient = %s, параметр historyPeriod = %s, в качестве индекса рынка выбран %s. ",
                    getName(),
                    scaleCoefficient,
                    historyPeriod,
                    indexTicker
                )
                .concat(indexIsRiseToday ? "Индекс рос в предыдущий день." : "Индекс не рос в предыдущий день."),
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
