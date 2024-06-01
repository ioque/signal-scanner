package ru.ioque.investfund.adapters.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

@Component
public class PsqlSignalJournal implements SignalJournal {
    private final Set<Signal> signals = new HashSet<>();
    private final Sinks.Many<Signal> signalSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public void publish(Signal signal) {
        signals.add(signal);
        signalSink.tryEmitNext(signal);
    }

    @Override
    public List<Signal> findAllBy(ScannerId scannerId) {
        return signals.stream().filter(signal -> signal.getScannerId().equals(scannerId)).toList();
    }

    @Override
    public Flux<Signal> stream() {
        return signalSink.asFlux();
    }
}
