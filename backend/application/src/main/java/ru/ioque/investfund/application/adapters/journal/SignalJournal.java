package ru.ioque.investfund.application.adapters.journal;

import java.util.List;

import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

public interface SignalJournal {
    void publish(Signal signal);
    List<Signal> findActualBy(ScannerId scannerId);
}
