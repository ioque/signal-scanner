package ru.ioque.investfund.application.modules.pipeline.subscriber;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.repository.SignalRepository;
import ru.ioque.investfund.application.modules.pipeline.core.Subscriber;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.event.SignalRegistered;

@Slf4j
@Component
@AllArgsConstructor
public class SignalRegistry implements Subscriber<Signal> {

    private final SignalRegistryContext context;
    private final SignalRepository signalRepository;

    @Override
    public void receive(Signal newSignal) {
        if (!context.isInitialized()) {
            throw new IllegalStateException("context is not initialized");
        }
        if (registryIsPossible(newSignal)) {
            newSignal.setId(signalRepository.nextId());
            context.addSignal(newSignal);
            log.info("signal registered {}", newSignal);
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
