package ru.ioque.investfund.fakes.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import ru.ioque.investfund.application.adapters.repository.SignalRepository;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.event.SignalEvent;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.identifier.SignalId;

public class FakeSignalRepository implements SignalRepository {

    public Set<SignalEvent> events = new TreeSet<>();

    @Override
    public void save(SignalEvent event) {
        events.add(event);
    }

    @Override
    public Optional<Signal> findById(SignalId signalId) {
        final Signal signal = Signal.builder().id(signalId).build();
        final List<SignalEvent> signalEvents = events.stream().filter(row -> row.getSignalId().equals(signalId)).toList();
        if (signalEvents.isEmpty()) {
            return Optional.empty();
        }
        signal.applyEvents(signalEvents);
        return Optional.of(signal);
    }

    @Override
    public List<Signal> getAll() {
        return events
            .stream()
            .collect(Collectors.groupingBy(SignalEvent::getSignalId))
            .entrySet()
            .stream()
            .map(entry -> {
                Signal signal = Signal.builder().id(entry.getKey()).build();
                signal.applyEvents(entry.getValue().stream().toList());
                return signal;
            })
            .toList();
    }

    @Override
    public List<Signal> findAllBy(ScannerId scannerId) {
        return getAll().stream().filter(signal -> signal.getScannerId().equals(scannerId)).toList();
    }

    @Override
    public SignalId nextId() {
        return SignalId.from(UUID.randomUUID());
    }
}
