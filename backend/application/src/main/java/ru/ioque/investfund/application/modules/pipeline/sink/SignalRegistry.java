package ru.ioque.investfund.application.modules.pipeline.sink;

import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.application.modules.pipeline.core.Sink;
import ru.ioque.investfund.domain.scanner.entity.Signal;

@Component
@AllArgsConstructor
public class SignalRegistry implements Sink<Signal> {
    private final SignalRegistryContext context;
    private final SignalJournal signalJournal;

    @Override
    public void consume(Signal newSignal) {
        if (!context.isInitialized()) {
            throw new IllegalStateException("context is not initialized");
        }
        if (registryIsPossible(newSignal)) {
            context.addSignal(newSignal);
            signalJournal.publish(newSignal);
        }
    }

    private boolean registryIsPossible(Signal newSignal) {
        if (containsSignal(newSignal)) {
            return false;
        }

        final Optional<Signal> signalSameByTicker = context
            .getSignalsBy(newSignal.getScannerId())
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
        return context
            .getSignalsBy(newSignal.getScannerId())
            .stream()
            .anyMatch(newSignal::sameByBusinessKey);
    }
}
