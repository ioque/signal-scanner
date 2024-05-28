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
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SectoralFuturesAlgorithm extends ScannerAlgorithm {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    Ticker futuresTicker;

    public SectoralFuturesAlgorithm(
        SectoralFuturesProperties properties
    ) {
        super(properties.getType().getName());
        this.futuresTicker = properties.getFuturesTicker();
        this.futuresOvernightScale = properties.getFuturesOvernightScale();
        this.stockOvernightScale = properties.getStockOvernightScale();
    }

    @Override
    public List<Signal> findSignals(List<InstrumentPerformance> instruments, LocalDateTime watermark) {
        final List<Signal> signals = new ArrayList<>();
        final boolean futuresIsRiseOvernight = getFuturesStatistic(instruments).isRiseOvernight(futuresOvernightScale);
        for (final InstrumentPerformance instrument : analyzeInstruments(instruments)) {
            final boolean riseOvernight = instrument.isRiseOvernight(stockOvernightScale);
            if (futuresIsRiseOvernight && riseOvernight) {
                signals.add(
                    Signal.builder()
                        .instrumentId(instrument.getInstrumentId())
                        .isBuy(true)
                        .summary("""
                            Тренд инструмента растущий;
                            Тренд фьючерса растущий;""")
                        .watermark(watermark)
                        .price(instrument.getIntradayPerformance().map(IntradayPerformance::getTodayLastPrice).orElse(0D))
                        .build()
                );
            }
        }
        return signals;
    }

    private InstrumentPerformance getFuturesStatistic(List<InstrumentPerformance> instruments) {
        return instruments
            .stream()
            .filter(row -> futuresTicker.equals(row.getTicker()))
            .findFirst()
            .orElseThrow(() -> new DomainException("Не добавлен фьючерс на основной товар сектора."));
    }

    private List<InstrumentPerformance> analyzeInstruments(List<InstrumentPerformance> instruments) {
        return instruments.stream().filter(row -> !row.getTicker().equals(futuresTicker)).toList();
    }
}
