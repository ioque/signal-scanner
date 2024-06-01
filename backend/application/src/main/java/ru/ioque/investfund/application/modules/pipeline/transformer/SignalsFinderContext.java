package ru.ioque.investfund.application.modules.pipeline.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.modules.pipeline.core.Context;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.value.InstrumentTradingState;

@Component
public class SignalsFinderContext implements Context {
    @Getter
    private boolean initialized = false;
    private final Map<ScannerId, SignalScanner> scanners = new HashMap<>();
    private final Map<InstrumentId, Set<ScannerId>> listeners = new HashMap<>();
    private final Map<InstrumentId, InstrumentTradingState> tradingStates = new HashMap<>();

    @Override
    public synchronized void reset() {
        this.initialized = false;
        this.tradingStates.clear();
        this.listeners.clear();
        this.scanners.clear();
    }

    public synchronized void initialize(
        List<SignalScanner> scanners,
        List<Instrument> instruments,
        List<AggregatedTotals> aggregatedTotals) {
        for (final SignalScanner scanner : scanners) {
            this.scanners.put(scanner.getId(), scanner);
            for (final InstrumentId instrumentId : scanner.getInstrumentIds()) {
                if (!this.listeners.containsKey(instrumentId)) {
                    this.listeners.put(instrumentId, new HashSet<>());
                }
                this.listeners.get(instrumentId).add(scanner.getId());
            }
        }
        final Map<InstrumentId, List<AggregatedTotals>> aggregatedTotalsGrouped = aggregatedTotals
            .stream()
            .collect(Collectors.groupingBy(AggregatedTotals::getInstrumentId));
        for (final Instrument instrument : instruments) {
            if (this.listeners.containsKey(instrument.getId())) {
                final InstrumentTradingState tradingState = InstrumentTradingState.from(
                    instrument.getId(),
                    instrument.getTicker(),
                    new TreeSet<>(aggregatedTotalsGrouped.getOrDefault(instrument.getId(), new ArrayList<>()))
                );
                this.tradingStates.put(instrument.getId(), tradingState);
            }
        }
        this.initialized = true;
    }

    public List<InstrumentTradingState> findTradingStatesBy(List<InstrumentId> instrumentIds) {
        return instrumentIds.stream().map(tradingStates::get).filter(Objects::nonNull).toList();
    }

    public Optional<InstrumentTradingState> getTradingState(InstrumentId instrumentId) {
        return Optional.ofNullable(tradingStates.get(instrumentId));
    }

    public void updateTradingState(InstrumentTradingState tradingState) {
        this.tradingStates.put(tradingState.getInstrumentId(), tradingState);
    }

    public List<SignalScanner> findScannersBy(InstrumentId instrumentId) {
        return listeners
            .getOrDefault(instrumentId, new HashSet<>())
            .stream()
            .map(scanners::get)
            .filter(Objects::nonNull)
            .toList();
    }
}
