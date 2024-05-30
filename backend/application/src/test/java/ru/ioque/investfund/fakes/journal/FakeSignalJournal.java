package ru.ioque.investfund.fakes.journal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import ru.ioque.investfund.domain.pipeline.Processor;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

public class FakeSignalJournal implements SignalJournal {
    public Set<Processor<Signal>> processors = new HashSet<>();
    public Set<Signal> signals = new HashSet<>();

    @Override
    public void publish(Signal signal) {
        signals.add(signal);
        processors.forEach(processor -> processor.process(signal));
    }

    @Override
    public List<Signal> findAllBy(ScannerId scannerId) {
        return stream().filter(signal -> signal.getScannerId().equals(scannerId)).toList();
    }

    @Override
    public void subscribe(Processor<Signal> processor) {
        processors.add(processor);
    }

    public Stream<Signal> stream() {
        return signals.stream();
    }
}
