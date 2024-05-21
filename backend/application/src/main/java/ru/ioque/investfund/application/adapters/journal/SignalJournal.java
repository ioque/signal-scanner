package ru.ioque.investfund.application.adapters.journal;

import ru.ioque.investfund.domain.scanner.entity.Signal;

public interface SignalJournal {
    void publish(Signal signal);
}
