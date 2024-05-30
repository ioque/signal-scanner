package ru.ioque.investfund.domain.pipeline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalProducerContext {
    Map<ScannerId, SignalScanner> scanners = new HashMap<>();
    Map<InstrumentId, Set<ScannerId>> subscribers = new HashMap<>();
    Map<Ticker, InstrumentId> tickerToIdMap = new HashMap<>();
    Map<InstrumentId, IntradayPerformance> intradayPerformances = new HashMap<>();
    Map<InstrumentId, TreeSet<AggregatedTotals>> aggregatedTotals = new HashMap<>();

    public SignalProducerContext(
        List<SignalScanner> scanners,
        List<Instrument> instruments,
        List<AggregatedTotals> aggregatedTotals) {

        for (final Instrument instrument : instruments) {
            tickerToIdMap.put(instrument.getTicker(), instrument.getId());
            this.intradayPerformances.put(instrument.getId(), IntradayPerformance.of(instrument.getTicker()));
            this.aggregatedTotals.put(instrument.getId(), new TreeSet<>());
        }
        for (final AggregatedTotals aggregatedTotal : aggregatedTotals) {
            this.aggregatedTotals
                .get(findIdBy(aggregatedTotal.getTicker()))
                .add(aggregatedTotal);
        }
        for (final SignalScanner scanner : scanners) {
            this.scanners.put(scanner.getId(), scanner);
            scanner.getInstrumentIds().forEach(instrumentId -> {
                if (!subscribers.containsKey(instrumentId)) {
                    subscribers.put(instrumentId, new HashSet<>());
                }
                subscribers.get(instrumentId).add(scanner.getId());
            });
        }
    }

    public List<SignalScanner> getSubscribers(Ticker ticker) {
        return subscribers
            .getOrDefault(findIdBy(ticker), new HashSet<>())
            .stream()
            .map(scanners::get)
            .toList();
    }

    public boolean containsTicker(Ticker ticker) {
        return tickerToIdMap.containsKey(ticker);
    }

    public void updateIntradayPerformance(IntradayData intradayData) {
        intradayPerformances.computeIfPresent(
            findIdBy(intradayData.getTicker()),
            (k, v) -> v.add(intradayData)
        );
    }

    public InstrumentPerformance getInstrumentPerformance(InstrumentId instrumentId) {
        final IntradayPerformance intradayPerformance = intradayPerformances.get(instrumentId);
        return InstrumentPerformance.builder()
            .instrumentId(instrumentId)
            .ticker(intradayPerformance.getTicker())
            .intradayPerformance(intradayPerformance)
            .aggregatedHistories(this.aggregatedTotals.get(instrumentId))
            .build();
    }

    public InstrumentId findIdBy(Ticker ticker) {
        return tickerToIdMap.get(ticker);
    }
}
