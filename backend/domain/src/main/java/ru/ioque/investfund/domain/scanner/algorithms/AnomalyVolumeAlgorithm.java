package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AnomalyVolumeAlgorithm extends ScannerAlgorithm {
    Double scaleCoefficient;
    Ticker indexTicker;
    Integer historyPeriod;

    public AnomalyVolumeAlgorithm(AnomalyVolumeProperties properties) {
        super(properties.getType().getName());
        this.scaleCoefficient = properties.getScaleCoefficient();
        this.historyPeriod = properties.getHistoryPeriod();
        this.indexTicker = properties.getIndexTicker();
    }

    @Override
    public List<Signal> findSignals(final List<TradingSnapshot> tradingSnapshots, final LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        final Optional<Boolean> indexIsRiseToday = getMarketIndex(tradingSnapshots).isRiseToday();

        if (indexIsRiseToday.isEmpty()) {
            return signals;
        }

        for (final TradingSnapshot tradingSnapshot : getAnalyzeStatistics(tradingSnapshots)) {
            final Optional<Double> medianValue = tradingSnapshot.getHistoryMedianValue(historyPeriod);
            final Double currentValue = tradingSnapshot.getValue();
            if (medianValue.isEmpty() || currentValue == null || tradingSnapshot.isRiseToday().isEmpty()) {
                continue;
            }
            final double currentValueToMedianValue = currentValue / medianValue.get();
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            if (currentValueToMedianValue > scaleCoefficient && indexIsRiseToday.get() && tradingSnapshot
                .isRiseToday()
                .get()) {
                signals.add(
                    Signal.builder()
                        .instrumentId(tradingSnapshot.getInstrumentId())
                        .isBuy(true)
                        .summary(createSummary(
                            formatter,
                            medianValue.get(),
                            currentValue,
                            currentValueToMedianValue,
                            true
                        ))
                        .watermark(watermark)
                        .price(tradingSnapshot.getLastPrice())
                        .build()
                );
            }
            if (currentValueToMedianValue > scaleCoefficient && !tradingSnapshot.isRiseToday().get()) {
                signals.add(
                    Signal.builder()
                        .instrumentId(tradingSnapshot.getInstrumentId())
                        .isBuy(false)
                        .watermark(watermark)
                        .summary(createSummary(
                            formatter,
                            medianValue.get(),
                            currentValue,
                            currentValueToMedianValue,
                            indexIsRiseToday.get()
                        ))
                        .price(tradingSnapshot.getLastPrice())
                        .build()
                );
            }
        }
        return signals;
    }

    private static String createSummary(
        DecimalFormat formatter,
        Double medianValue,
        Double currentValue,
        double currentValueToMedianValue,
        Boolean indexIsRiseToday
    ) {
        return String.format(
            """
                Медиана исторических объемов: %s;
                Текущий объем: %s;
                Отношение текущего объема к медиане: %s;
                Тренд индекса %s.""",
            formatter.format(medianValue),
            formatter.format(currentValue),
            formatter.format(currentValueToMedianValue),
            indexIsRiseToday ? "растущий" : "нисходящий"
        );
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
}
