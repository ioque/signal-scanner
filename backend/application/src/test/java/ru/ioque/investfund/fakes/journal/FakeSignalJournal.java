package ru.ioque.investfund.fakes.journal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

public class FakeSignalJournal implements SignalJournal {
    public Set<Signal> signals = new HashSet<>();
    Sinks.Many<Signal> signalSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public void publish(Signal signal) {
        signals.add(signal);
        signalSink.tryEmitNext(signal);
    }

    @Override
    public List<Signal> findAllBy(ScannerId scannerId) {
        return getAll().filter(signal -> signal.getScannerId().equals(scannerId)).toList();
    }

    @Override
    public Flux<Signal> stream() {
        return signalSink.asFlux();
    }

    public Stream<Signal> getAll() {
        return signals.stream();
    }
}
