package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.journal.EmulatedPositionJournal;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPositionId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeEmulatedPositionJournal implements EmulatedPositionJournal {
    public List<EmulatedPosition> emulatedPositions = new CopyOnWriteArrayList<>();

    @Override
    public void publish(EmulatedPosition emulatedPosition) {
        emulatedPositions.add(emulatedPosition);
    }

    @Override
    public List<EmulatedPosition> findAllBy(InstrumentId instrumentId) {
        return emulatedPositions.stream().toList();
    }

    @Override
    public Optional<EmulatedPosition> findActualBy(InstrumentId instrumentId, ScannerId scannerId) {
        return findAllBy(instrumentId)
            .stream()
            .filter(row -> row.getScanner().getId().equals(scannerId) && row.getInstrument().getId().equals(instrumentId))
            .findFirst();
    }


    @Override
    public EmulatedPositionId nextId() {
        return EmulatedPositionId.from(UUID.randomUUID());
    }
}
