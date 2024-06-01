package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.repository.EmulatedPositionRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.position.Position;
import ru.ioque.investfund.domain.position.PositionId;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeEmulatedPositionRepository implements EmulatedPositionRepository {
    public List<Position> positions = new CopyOnWriteArrayList<>();

    @Override
    public void save(Position position) {
        positions.add(position);
    }

    @Override
    public List<Position> findAllBy(InstrumentId instrumentId) {
        return positions.stream().toList();
    }

    @Override
    public Optional<Position> findActualBy(InstrumentId instrumentId, ScannerId scannerId) {
        return findAllBy(instrumentId)
            .stream()
            .filter(row -> row.getScannerId().equals(scannerId) && row.getInstrumentId().equals(instrumentId))
            .findFirst();
    }

    @Override
    public PositionId nextId() {
        return PositionId.from(UUID.randomUUID());
    }
}
