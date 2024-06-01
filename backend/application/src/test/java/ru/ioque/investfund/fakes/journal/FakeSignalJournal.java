package ru.ioque.investfund.fakes.journal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

public class FakeSignalJournal implements SignalJournal {
    public Set<Signal> signals = new HashSet<>();

    @Override
    public void publish(Signal signal) {
        signals.add(signal);
    }

    @Override
    public List<Signal> findAllBy(ScannerId scannerId) {
        return getAll().filter(signal -> signal.getScannerId().equals(scannerId)).toList();
    }

    public Stream<Signal> getAll() {
        return signals.stream();
    }
}
