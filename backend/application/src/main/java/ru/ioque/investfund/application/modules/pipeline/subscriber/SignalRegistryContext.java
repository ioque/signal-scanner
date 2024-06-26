package ru.ioque.investfund.application.modules.pipeline.subscriber;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.modules.pipeline.core.Context;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;

@Component
public class SignalRegistryContext implements Context {
    @Getter
    private boolean initialized = false;

    private final Map<ScannerId, Set<Signal>> signals = new HashMap<>();

    @Override
    public synchronized void reset() {
        this.initialized = false;
        this.signals.clear();
    }

    public synchronized void initialize(List<Signal> signals) {
        signals.forEach(this::addSignal);
        initialized = true;
    }

    public List<Signal> getSignalsBy(ScannerId scannerId) {
        return signals.getOrDefault(scannerId, new HashSet<>()).stream().toList();
    }

    public List<Signal> getSignalsBy(InstrumentId instrumentId) {
        return signals
            .values()
            .stream()
            .flatMap(Collection::stream)
            .filter(signal -> signal.getInstrumentId().equals(instrumentId))
            .toList();
    }

    public void addSignal(Signal signal) {
        if (!this.signals.containsKey(signal.getScannerId())) {
            this.signals.put(signal.getScannerId(), new HashSet<>());
        }
        this.signals.get(signal.getScannerId()).add(signal);
    }
}
