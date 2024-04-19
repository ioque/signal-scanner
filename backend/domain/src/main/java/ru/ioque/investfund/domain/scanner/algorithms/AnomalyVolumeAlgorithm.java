package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnomalyVolumeAlgorithm extends ScannerAlgorithm {
    Double scaleCoefficient;
    InstrumentId indexId;
    Integer historyPeriod;

    public AnomalyVolumeAlgorithm(AnomalyVolumeProperties properties) {
        super(properties.getType().getName());
        setScaleCoefficient(properties.getScaleCoefficient());
        setHistoryPeriod(properties.getHistoryPeriod());
        setIndexId(properties.getIndexId());
    }

    @Override
    public List<Signal> run(final List<TradingSnapshot> tradingSnapshots, final LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        final Optional<Boolean> indexIsRiseToday = getMarketIndex(tradingSnapshots).isRiseToday();
        for (final TradingSnapshot tradingSnapshot : getAnalyzeStatistics(tradingSnapshots)) {
            final Optional<Double> medianValue = tradingSnapshot.getHistoryMedianValue(historyPeriod);
            final Optional<Double> currentValue = tradingSnapshot.getTodayValue();
            if (medianValue.isEmpty() ||
                currentValue.isEmpty() ||
                indexIsRiseToday.isEmpty() ||
                tradingSnapshot.isRiseToday().isEmpty()) {
                continue;
            }
            final double currentValueToMedianValue = currentValue.get() / medianValue.get();
            final String summary = String.format(
                "медиана исторических объемов: %s; текущий объем: %s; отношение текущего объема к медиане: %s; тренд индекса %s.",
                medianValue.get(),
                currentValue.get(),
                currentValueToMedianValue,
                indexIsRiseToday.get() ? "растущий" : "нисходящий"
            );
            if (currentValueToMedianValue > scaleCoefficient && indexIsRiseToday.get() && tradingSnapshot.isRiseToday().get()) {
                signals.add(
                    Signal.builder()
                        .isOpen(true)
                        .isBuy(true)
                        .summary(summary)
                        .dateTime(watermark)
                        .instrumentId(tradingSnapshot.getInstrumentId())
                        .price(tradingSnapshot.getTodayLastPrice().orElse(0D))
                        .build()
                );
            }
            if (currentValueToMedianValue > scaleCoefficient && !tradingSnapshot.isRiseToday().get()) {
                signals.add(
                    Signal.builder()
                        .isOpen(true)
                        .isBuy(false)
                        .dateTime(watermark)
                        .summary(summary)
                        .price(tradingSnapshot.getTodayLastPrice().orElse(0D))
                        .instrumentId(tradingSnapshot.getInstrumentId())
                        .build()
                );
            }
        }
        return signals;
    }

    private List<TradingSnapshot> getAnalyzeStatistics(List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots.stream().filter(row -> !row.getInstrumentId().equals(indexId)).toList();
    }

    private TradingSnapshot getMarketIndex(final List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(row -> row.getInstrumentId().equals(indexId))
            .findFirst()
            .orElseThrow(() -> new DomainException("Не добавлен индекс рынка."));
    }

    private void setScaleCoefficient(Double scaleCoefficient) {
        if (scaleCoefficient == null) {
            throw new DomainException("Не передан параметр scaleCoefficient.");
        }
        if (scaleCoefficient <= 0) {
            throw new DomainException("Параметр scaleCoefficient должен быть больше нуля.");
        }
        this.scaleCoefficient = scaleCoefficient;
    }

    private void setIndexId(InstrumentId indexId) {
        if (indexId == null) {
            throw new DomainException("Не передан идентификатор индекса.");
        }
        this.indexId = indexId;
    }

    private void setHistoryPeriod(Integer historyPeriod) {
        if (historyPeriod == null) {
            throw new DomainException("Не передан параметр historyPeriod.");
        }
        if (historyPeriod <= 0) {
            throw new DomainException("Параметр historyPeriod должен быть больше нуля.");
        }
        this.historyPeriod = historyPeriod;
    }
}
