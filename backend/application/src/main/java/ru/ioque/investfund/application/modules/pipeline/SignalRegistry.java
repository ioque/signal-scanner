package ru.ioque.investfund.application.modules.pipeline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalRegistry {
    @Getter
    boolean initialized = false;
    final Map<ScannerId, Set<Signal>> registeredSignals = new HashMap<>();

    public void initialize(List<Signal> signals) {
        registeredSignals.clear();
        signals.forEach(this::register);
        initialized = true;
    }

    public boolean register(Signal newSignal) {
        if (!validate(newSignal)) {
            return false;
        }
        addSignal(newSignal);
        return true;
    }

    private void addSignal(Signal newSignal) {
        if (!registeredSignals.containsKey(newSignal.getScannerId())) {
            registeredSignals.put(newSignal.getScannerId(), new HashSet<>());
        }
        registeredSignals.get(newSignal.getScannerId()).add(newSignal);
    }

    private boolean validate(Signal newSignal) {
        if (containsSignal(newSignal)) {
            return false;
        }

        final Optional<Signal> signalSameByTicker = registeredSignals
            .getOrDefault(newSignal.getScannerId(), new HashSet<>())
            .stream()
            .filter(signal -> signal.sameByTicker(newSignal))
            .findFirst();

        if (signalSameByTicker.isPresent()) {
            if (signalSameByTicker.get().isBuy() && newSignal.isSell()) {
                return true;
            }
            return signalSameByTicker.get().isSell() && newSignal.isBuy();
        }

        return newSignal.isBuy();
    }

    private boolean containsSignal(Signal newSignal) {
        return registeredSignals
            .getOrDefault(newSignal.getScannerId(), new HashSet<>())
            .stream()
            .anyMatch(newSignal::sameByBusinessKey);
    }
}
