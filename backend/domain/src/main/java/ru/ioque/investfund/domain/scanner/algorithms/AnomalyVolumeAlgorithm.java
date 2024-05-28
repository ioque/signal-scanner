package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;

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
    public List<Signal> findSignals(final List<InstrumentPerformance> instruments, final LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        final Optional<Boolean> indexIsRiseToday = getMarketIndex(instruments).isRiseToday();

        if (indexIsRiseToday.isEmpty()) {
            return signals;
        }

        for (final InstrumentPerformance instrument : getAnalyzeStatistics(instruments)) {
            final Optional<Double> medianValue = instrument.getHistoryMedianValue(historyPeriod);
            final Optional<Double> currentValue = instrument.getIntradayPerformance().map(IntradayPerformance::getTodayValue);
            if (medianValue.isEmpty() || currentValue.isEmpty() || instrument.isRiseToday().isEmpty()) {
                continue;
            }
            final double currentValueToMedianValue = currentValue.get() / medianValue.get();
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            if (currentValueToMedianValue > scaleCoefficient && indexIsRiseToday.get() && instrument
                .isRiseToday()
                .get()) {
                signals.add(
                    Signal.builder()
                        .instrumentId(instrument.getInstrumentId())
                        .isBuy(true)
                        .summary(createSummary(
                            formatter,
                            medianValue.get(),
                            currentValue.get(),
                            currentValueToMedianValue,
                            true
                        ))
                        .watermark(watermark)
                        .price(instrument.getIntradayPerformance().map(IntradayPerformance::getTodayLastPrice).orElse(0D))
                        .build()
                );
            }
            if (currentValueToMedianValue > scaleCoefficient && !instrument.isRiseToday().get()) {
                signals.add(
                    Signal.builder()
                        .instrumentId(instrument.getInstrumentId())
                        .isBuy(false)
                        .watermark(watermark)
                        .summary(createSummary(
                            formatter,
                            medianValue.get(),
                            currentValue.get(),
                            currentValueToMedianValue,
                            indexIsRiseToday.get()
                        ))
                        .price(instrument.getIntradayPerformance().map(IntradayPerformance::getTodayLastPrice).orElse(0D))
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

    private List<InstrumentPerformance> getAnalyzeStatistics(List<InstrumentPerformance> instruments) {
        return instruments.stream().filter(row -> !row.getTicker().equals(indexTicker)).toList();
    }

    private InstrumentPerformance getMarketIndex(final List<InstrumentPerformance> instruments) {
        return instruments
            .stream()
            .filter(row -> row.getTicker().equals(indexTicker))
            .findFirst()
            .orElseThrow(() -> new DomainException("Не добавлен индекс рынка."));
    }
}
