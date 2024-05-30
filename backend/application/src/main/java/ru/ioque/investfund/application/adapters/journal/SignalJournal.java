package ru.ioque.investfund.application.adapters.journal;

import java.util.List;

import ru.ioque.investfund.application.modules.pipeline.core.Journal;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

public interface SignalJournal extends Journal<Signal> {
    void publish(Signal signal);
    List<Signal> findAllBy(ScannerId scannerId);
}
