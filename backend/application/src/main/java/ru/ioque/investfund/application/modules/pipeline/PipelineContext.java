package ru.ioque.investfund.application.modules.pipeline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.position.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PipelineContext {
    @Getter
    boolean initialized = false;
    final Map<ScannerId, SignalScanner> scanners = new HashMap<>();
    final Map<InstrumentId, Set<ScannerId>> subscribers = new HashMap<>();
    final Map<Ticker, InstrumentId> tickerToIdMap = new HashMap<>();
    final Map<InstrumentId, IntradayPerformance> intradayPerformances = new HashMap<>();
    final Map<InstrumentId, TreeSet<AggregatedTotals>> aggregatedTotals = new HashMap<>();
    final Map<InstrumentId, Set<EmulatedPosition>> emulatedPositions = new HashMap<>();

    public void initialize(
        List<SignalScanner> scanners,
        List<Instrument> instruments,
        List<AggregatedTotals> aggregatedTotals
    ) {
        for (final Instrument instrument : instruments) {
            tickerToIdMap.put(instrument.getTicker(), instrument.getId());
            this.intradayPerformances.put(instrument.getId(), IntradayPerformance.of(instrument.getTicker()));
            this.aggregatedTotals.put(instrument.getId(), new TreeSet<>());
        }
        for (final AggregatedTotals aggregatedTotal : aggregatedTotals) {
            this.aggregatedTotals
                .get(findInstrumentId(aggregatedTotal.getTicker()))
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
        this.initialized = true;
    }

    public List<SignalScanner> getSubscribers(Ticker ticker) {
        return subscribers
            .getOrDefault(findInstrumentId(ticker), new HashSet<>())
            .stream()
            .map(scanners::get)
            .toList();
    }

    public SignalScanner getScanner(ScannerId scannerId) {
        return scanners.get(scannerId);
    }

    public void updateIntradayPerformance(IntradayData intradayData) {
        intradayPerformances.computeIfPresent(
            findInstrumentId(intradayData.getTicker()),
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

    public InstrumentId findInstrumentId(Ticker ticker) {
        return tickerToIdMap.get(ticker);
    }

    public Optional<EmulatedPosition> getEmulatedPosition(InstrumentId instrumentId, ScannerId scannerId) {
        return emulatedPositions
            .getOrDefault(instrumentId, new HashSet<>())
            .stream()
            .filter(row -> row.getScannerId().equals(scannerId))
            .findFirst();
    }

    public List<EmulatedPosition> getAllEmulatedPositionBy(InstrumentId instrumentId) {
        return emulatedPositions.getOrDefault(instrumentId, new HashSet<>()).stream().toList();
    }

    public void addEmulatedPosition(EmulatedPosition emulatedPosition) {
        final InstrumentId instrumentId = emulatedPosition.getInstrumentId();
        final ScannerId scannerId = emulatedPosition.getScannerId();
        if (!emulatedPositions.containsKey(instrumentId)) {
            emulatedPositions.put(instrumentId, new HashSet<>());
        }
        getEmulatedPosition(instrumentId, scannerId).ifPresent(position -> emulatedPositions.get(instrumentId).remove(position));
        emulatedPositions.get(emulatedPosition.getInstrumentId()).add(emulatedPosition);
    }
}
