package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.journal.EmulatedPositionJournal;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.position.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
            .filter(row -> row.getScannerId().equals(scannerId) && row.getInstrumentId().equals(instrumentId))
            .findFirst();
    }
}
