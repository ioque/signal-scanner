package ru.ioque.investfund.domain.scanner.value.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.value.PrefSimplePair;
import ru.ioque.investfund.domain.scanner.value.SignalSign;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.PrefCommonProperties;

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
    public List<SignalSign> run(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        final List<SignalSign> signalSigns = new ArrayList<>();
        findAllPrefAndSimplePairs(tradingSnapshots).forEach(pair -> {
            final double currentDelta = pair.getCurrentDelta();
            final double historyDelta = pair.getHistoryDelta();
            final double multiplier = currentDelta / historyDelta;
            final String summary = String.format(
                "Текущая дельта: %s; историческая дельта: %s; отношение текущей дельты к исторической: %s.",
                currentDelta, historyDelta, multiplier
            );
            if (multiplier > spreadValue) {
                signalSigns.add(SignalSign.builder()
                    .isBuy(true)
                    .summary(summary)
                    .dateTime(watermark)
                    .ticker(pair.getPref().getTicker())
                    .price(pair.getPref().getTodayLastPrice().orElse(0D))
                    .build()
                );
            }
        });
        return signalSigns;
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
