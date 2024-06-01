package ru.ioque.investfund.application.modules.pipeline.sink;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.repository.SignalRepository;
import ru.ioque.investfund.application.modules.pipeline.core.Sink;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.event.SignalRegistered;

@Component
@AllArgsConstructor
public class SignalRegistry implements Sink<Signal> {

    private final SignalRegistryContext context;
    private final SignalRepository signalRepository;

    @Override
    public void consume(Signal newSignal) {
        if (!context.isInitialized()) {
            throw new IllegalStateException("context is not initialized");
        }
        if (registryIsPossible(newSignal)) {
            newSignal.setId(signalRepository.nextId());
            context.addSignal(newSignal);
            signalRepository.save(
                SignalRegistered.builder()
                    .signalId(newSignal.getId())
                    .instrumentId(newSignal.getInstrumentId())
                    .scannerId(newSignal.getScannerId())
                    .summary(newSignal.getSummary())
                    .openPrice(newSignal.getOpenPrice())
                    .timestamp(newSignal.getTimestamp())
                    .build()
            );
        }
    }

    private boolean registryIsPossible(Signal newSignal) {
        return context
            .getSignalsBy(newSignal.getScannerId())
            .stream()
            .noneMatch(signal -> signal.sameByInstrumentId(newSignal) && signal.sameByScannerId(newSignal));
    }
}
