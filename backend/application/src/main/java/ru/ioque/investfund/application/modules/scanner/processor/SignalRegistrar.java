package ru.ioque.investfund.application.modules.scanner.processor;

import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.domain.scanner.entity.Signal;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalRegistrar {
    SignalJournal signalJournal;

    public void registerSignal(Signal signal) {
        List<Signal> existedSignals = signalJournal.findAllBy(signal.getScannerId());
        if (containsSignal(existedSignals, signal)) {
            return;
        }
        registerNewSignal(existedSignals, signal);
    }

    private boolean containsSignal(List<Signal> existedSignals, Signal newSignal) {
        return existedSignals.stream().anyMatch(newSignal::sameByBusinessKey);
    }

    private void registerNewSignal(List<Signal> existedSignals, Signal newSignal) {
        final Optional<Signal> signalSameByTicker = existedSignals
            .stream()
            .filter(signal -> signal.sameByTicker(newSignal))
            .findFirst();

        if (signalSameByTicker.isPresent()) {
            if (signalSameByTicker.get().isBuy() && newSignal.isSell()) {
                signalJournal.publish(newSignal);
                return;
            }
            if (signalSameByTicker.get().isSell() && newSignal.isBuy()) {
                signalJournal.publish(newSignal);
                return;
            }
            return;
        }
        if (newSignal.isBuy()) {
            signalJournal.publish(newSignal);
        }
    }
}
