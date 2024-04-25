package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.PrefSimplePair;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PrefCommonAlgorithm extends ScannerAlgorithm {
    final Double spreadValue;

    public PrefCommonAlgorithm(PrefCommonProperties properties) {
        super(properties.getType().getName());
        this.spreadValue = properties.getSpreadValue();
    }

    @Override
    public ScanningResult run(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        ScanningResult scanningResult = new ScanningResult();
        findAllPrefAndSimplePairs(tradingSnapshots).forEach(pair -> {
            final double currentDelta = pair.getCurrentDelta();
            final double historyDelta = pair.getHistoryDelta();
            final double multiplier = currentDelta / historyDelta;
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            final String summary = String.format(
                """
                Текущая дельта: %s;
                Историческая дельта: %s;
                Отношение текущей дельты к исторической: %s.""",
                formatter.format(currentDelta), formatter.format(historyDelta), formatter.format(multiplier)
            );
            scanningResult.addLog(String.format("Пара преф[ticker=%s] обычка[ticker=%s] | %s", pair.getPref().getTicker(), pair.getSimple().getTicker(), summary));
            if (multiplier > spreadValue) {
                scanningResult.addSignal(Signal.builder()
                    .instrumentId(pair.getPref().getInstrumentId())
                    .isOpen(true)
                    .isBuy(true)
                    .summary(summary)
                    .watermark(watermark)
                    .price(pair.getPref().getLastPrice())
                    .build()
                );
            }
        });
        return scanningResult;
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
            .orElseThrow(() -> new DomainException(
                "Для привилегированной акции " + tradingSnapshot.getTicker() + " не найдена обычная акция."));
    }
}
