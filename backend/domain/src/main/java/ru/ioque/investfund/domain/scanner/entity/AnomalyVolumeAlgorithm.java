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
import java.util.Optional;
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
public class AnomalyVolumeAlgorithm extends ScannerAlgorithm {
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
    public ScanningResult run(UUID scannerId, final List<TradingSnapshot> tradingSnapshots, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<ScannerLog> logs = new ArrayList<>();
        final Optional<Boolean> indexIsRiseToday = getMarketIndex(tradingSnapshots).isRiseToday();
        logs.add(runWorkMessage(indexIsRiseToday.orElse(null)));
        for (final TradingSnapshot tradingSnapshot : getAnalyzeStatistics(tradingSnapshots)) {
            final Optional<Double> medianHistoryValue = tradingSnapshot.getHistoryMedianValue();
            final Optional<Double> value = tradingSnapshot.getTodayValue();
            if (medianHistoryValue.isEmpty() || value.isEmpty() || indexIsRiseToday.isEmpty() || tradingSnapshot
                .isRiseToday().isEmpty()) continue;
            final double multiplier = value.get() / medianHistoryValue.get();
            logs.add(parametersMessage(tradingSnapshot, value.get(), medianHistoryValue.get(), multiplier));
            if (multiplier > scaleCoefficient && indexIsRiseToday.get() && tradingSnapshot.isRiseToday().get()) {
                signals.add(new Signal(dateTimeNow, tradingSnapshot.getTicker(), true));
            }
            if (multiplier > scaleCoefficient && !tradingSnapshot.isRiseToday().get()) {
                signals.add(new Signal(dateTimeNow, tradingSnapshot.getTicker(), false));
            }
        }
        logs.add(finishWorkMessage(signals));
        return ScanningResult.builder()
            .dateTime(dateTimeNow)
            .signals(signals)
            .logs(logs)
            .build();
    }

    private List<TradingSnapshot> getAnalyzeStatistics(List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots.stream().filter(row -> !row.getTicker().equals(indexTicker)).toList();
    }

    private TradingSnapshot getMarketIndex(final List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(row -> row.getTicker().equals(indexTicker))
            .findFirst()
            .orElseThrow(() -> new DomainException("Не добавлен индекс рынка."));
    }

    private ScannerLog parametersMessage(
        TradingSnapshot tradingSnapshot,
        double value,
        double waHistoryValue,
        double multiplier
    ) {
        return new ScannerLog(
            String.format(
                "Инструмент %s, текущий объем торгов внутри дня: %s, средневзвешенный исторический объем: %s, отношение текущего объема к историческому: %s.",
                tradingSnapshot.getTicker(),
                value,
                waHistoryValue,
                multiplier
            ),
            LocalDateTime.now()
        );
    }

    private ScannerLog runWorkMessage(Boolean indexIsRiseToday) {
        return new ScannerLog(
            String
                .format(
                    "Начата обработка данных по алгоритму %s. Параметр scaleCoefficient = %s, параметр historyPeriod = %s, в качестве индекса рынка выбран %s. ",
                    getName(),
                    scaleCoefficient,
                    historyPeriod,
                    indexTicker
                )
                .concat(indexIsRiseToday == null ? "Данных по индексу нет." : (indexIsRiseToday ? "Индекс рос в предыдущий день." : "Индекс не рос в предыдущий день.")),
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
