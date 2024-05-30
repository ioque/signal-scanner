package ru.ioque.investfund.domain.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalRegistry {
    Map<ScannerId, Set<Signal>> registeredSignals = new HashMap<>();

    public SignalRegistry(List<Signal> signals) {
        signals.forEach(this::register);
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
