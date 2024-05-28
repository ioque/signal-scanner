package ru.ioque.investfund.fakes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

public class FakeSignalJournal implements SignalJournal {
    public List<Signal> signals = new ArrayList<>();

    @Override
    public void publish(Signal signal) {
        signals.add(signal);
    }

    @Override
    public List<Signal> getBy(ScannerId scannerId) {
        return stream().filter(signal -> signal.getScannerId().equals(scannerId)).toList();
    }

    public Stream<Signal> stream() {
        return signals.stream();
    }
}
