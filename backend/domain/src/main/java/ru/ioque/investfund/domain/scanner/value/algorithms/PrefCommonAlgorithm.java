package ru.ioque.investfund.domain.scanner.value.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.PrefSimplePair;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.TickerSummary;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.PrefCommonProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public ScanningResult run(UUID scannerId, List<TradingSnapshot> tradingSnapshots, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<TickerSummary> tickerSummaries = new ArrayList<>();
        findAllPrefAndSimplePairs(tradingSnapshots).forEach(pair -> {
            final double currentDelta = pair.getCurrentDelta();
            final double historyDelta = pair.getHistoryDelta();
            final double multiplier = currentDelta / historyDelta;
            if (multiplier > spreadValue) {
                signals.add(new Signal(dateTimeNow, pair.getPref().getTicker(), true));
            }
            tickerSummaries.add(
                new TickerSummary(
                    pair.getPref().getTicker(),
                    String.format(
                        "Текущая дельта: %s; историческая дельта: %s; отношение текущей дельты к исторической: %s.",
                        currentDelta, historyDelta, multiplier
                    )
                )
            );
        });
        return ScanningResult.builder()
            .dateTime(dateTimeNow)
            .signals(signals)
            .tickerSummaries(tickerSummaries)
            .build();
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
            .orElseThrow(() -> new DomainException("Для привилегированной акции " + tradingSnapshot.getTicker() + " не найдена обычная акция."));
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
