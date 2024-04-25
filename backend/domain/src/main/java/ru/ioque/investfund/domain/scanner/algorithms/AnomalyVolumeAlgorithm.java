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
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
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
    public ScanningResult run(final List<TradingSnapshot> tradingSnapshots, final LocalDateTime watermark) {
        ScanningResult scanningResult = new ScanningResult();
        final Optional<Boolean> indexIsRiseToday = getMarketIndex(tradingSnapshots).isRiseToday();
        for (final TradingSnapshot tradingSnapshot : getAnalyzeStatistics(tradingSnapshots)) {
            final Optional<Double> medianValue = tradingSnapshot.getHistoryMedianValue(historyPeriod);
            final Double currentValue = tradingSnapshot.getValue();
            if (medianValue.isEmpty() ||
                currentValue == null ||
                indexIsRiseToday.isEmpty() ||
                tradingSnapshot.isRiseToday().isEmpty()) {
                continue;
            }
            final double currentValueToMedianValue = currentValue / medianValue.get();
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            final String summary = String.format(
                """
                Медиана исторических объемов: %s;
                Текущий объем: %s;
                Отношение текущего объема к медиане: %s;
                Тренд индекса %s.""",
                formatter.format(medianValue.get()),
                formatter.format(currentValue),
                formatter.format(currentValueToMedianValue),
                indexIsRiseToday.get() ? "растущий" : "нисходящий"
            );
            scanningResult.addLog(String.format("Инструмент[ticker=%s] | %s", tradingSnapshot.getTicker(), summary));
            if (currentValueToMedianValue > scaleCoefficient && indexIsRiseToday.get() && tradingSnapshot
                .isRiseToday()
                .get()) {
                scanningResult.addSignal(
                    Signal.builder()
                        .instrumentId(tradingSnapshot.getInstrumentId())
                        .isOpen(true)
                        .isBuy(true)
                        .summary(summary)
                        .watermark(watermark)
                        .price(tradingSnapshot.getLastPrice())
                        .build()
                );
            }
            if (currentValueToMedianValue > scaleCoefficient && !tradingSnapshot.isRiseToday().get()) {
                scanningResult.addSignal(
                    Signal.builder()
                        .instrumentId(tradingSnapshot.getInstrumentId())
                        .isOpen(true)
                        .isBuy(false)
                        .watermark(watermark)
                        .summary(summary)
                        .price(tradingSnapshot.getLastPrice())
                        .build()
                );
            }
        }
        return scanningResult;
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
