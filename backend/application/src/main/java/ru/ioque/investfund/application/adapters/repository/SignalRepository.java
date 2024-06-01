package ru.ioque.investfund.application.adapters.repository;

import java.util.List;
import java.util.Optional;

import ru.ioque.investfund.domain.scanner.entity.event.SignalEvent;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.identifier.SignalId;

public interface SignalRepository {
    void save(SignalEvent event);
    Optional<Signal> findById(SignalId signalId);
    List<Signal> getAll();
    List<Signal> findAllBy(ScannerId scannerId);
    SignalId nextId();
}
