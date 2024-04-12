package ru.ioque.investfund.domain.scanner.value.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.TickerSummary;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.SectoralFuturesProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralFuturesAlgorithm extends ScannerAlgorithm {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;

    public SectoralFuturesAlgorithm(
        SectoralFuturesProperties properties
    ) {
        super(properties.getType().getName());
        setFuturesOvernightScale(properties.getFuturesOvernightScale());
        setStockOvernightScale(properties.getStockOvernightScale());
        setFuturesTicker(properties.getFuturesTicker());
    }

    @Override
    public ScanningResult run(UUID scannerId, List<TradingSnapshot> tradingSnapshots, LocalDateTime dateTimeNow) {
        List<Signal> signals = new ArrayList<>();
        List<TickerSummary> tickerSummaries = new ArrayList<>();
        boolean futuresIsRiseOvernight = getFuturesStatistic(tradingSnapshots).isRiseOvernight(futuresOvernightScale);
        tickerSummaries.add(
            new TickerSummary(
                getFuturesStatistic(tradingSnapshots).getTicker(),
                String.format(
                    "тренд %s;",
                    (futuresIsRiseOvernight ? "растущий" : "нисходящий")
                )
            )
        );
        for (var finInstrument : analyzeInstruments(tradingSnapshots)) {
            boolean riseOvernight = finInstrument.isRiseOvernight(stockOvernightScale);
            if (futuresIsRiseOvernight && riseOvernight) {
                signals.add(new Signal(dateTimeNow, finInstrument.getTicker(), true));
            }
            tickerSummaries.add(
                new TickerSummary(
                    finInstrument.getTicker(),
                    String.format(
                        "тренд %s;",
                        (riseOvernight ? "растущий" : "нисходящий")
                    )
                )
            );
        }
        return ScanningResult.builder()
            .dateTime(dateTimeNow)
            .signals(signals)
            .tickerSummaries(tickerSummaries)
            .build();
    }

    private TradingSnapshot getFuturesStatistic(List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(row -> futuresTicker.equals(row.getTicker()))
            .findFirst()
            .orElseThrow(() -> new DomainException("Не добавлен фьючерс на основной товар сектора."));
    }

    private List<TradingSnapshot> analyzeInstruments(List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots.stream().filter(row -> !row.getTicker().equals(futuresTicker)).toList();
    }

    private void setFuturesOvernightScale(Double futuresOvernightScale) {
        if (futuresOvernightScale == null) {
            throw new DomainException("Не передан параметр futuresOvernightScale.");
        }
        if (futuresOvernightScale <= 0) {
            throw new DomainException("Параметр futuresOvernightScale должен быть больше нуля.");
        }
        this.futuresOvernightScale = futuresOvernightScale;
    }

    private void setStockOvernightScale(Double stockOvernightScale) {
        if (stockOvernightScale == null) {
            throw new DomainException("Не передан параметр stockOvernightScale.");
        }
        if (stockOvernightScale <= 0) {
            throw new DomainException("Параметр stockOvernightScale должен быть больше нуля.");
        }
        this.stockOvernightScale = stockOvernightScale;
    }

    private void setFuturesTicker(String futuresTicker) {
        if (futuresTicker == null || futuresTicker.isEmpty()) {
            throw new DomainException("Не передан параметр futuresTicker.");
        }
        this.futuresTicker = futuresTicker;
    }
}
