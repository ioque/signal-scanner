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
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefCommonAlgorithm extends ScannerAlgorithm {
    Double spreadValue;

    public PrefCommonAlgorithm(PrefCommonProperties properties) {
        super(properties.getType().getName());
        setSpreadValue(properties.getSpreadValue());
    }

    @Override
    public List<Signal> run(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        findAllPrefAndSimplePairs(tradingSnapshots).forEach(pair -> {
            final double currentDelta = pair.getCurrentDelta();
            final double historyDelta = pair.getHistoryDelta();
            final double multiplier = currentDelta / historyDelta;
            final String summary = String.format(
                "Текущая дельта: %s; историческая дельта: %s; отношение текущей дельты к исторической: %s.",
                currentDelta, historyDelta, multiplier
            );
            if (multiplier > spreadValue) {
                signals.add(Signal.builder()
                    .isOpen(true)
                    .isBuy(true)
                    .summary(summary)
                    .watermark(watermark)
                    .instrumentId(pair.getPref().getInstrumentId())
                    .price(pair.getPref().getTodayLastPrice().orElse(0D))
                    .build()
                );
            }
        });
        return signals;
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
                "Для привилегированной акции " + tradingSnapshot.getInstrumentId() + " не найдена обычная акция."));
    }

    private void setSpreadValue(Double spreadValue) {
        if (spreadValue == null) {
            throw new DomainException("Не передан параметр spreadParam.");
        }
        if (spreadValue <= 0) {
            throw new DomainException("Параметр spreadParam должен быть больше нуля.");
        }
        this.spreadValue = spreadValue;
    }
}
