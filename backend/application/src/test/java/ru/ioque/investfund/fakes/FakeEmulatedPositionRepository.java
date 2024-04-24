package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.EmulatedPositionRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPositionId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeEmulatedPositionRepository implements EmulatedPositionRepository {
    Map<EmulatedPositionId, EmulatedPosition> emulatedPositions = new ConcurrentHashMap<>();

    @Override
    public List<EmulatedPosition> findAllBy(InstrumentId instrumentId) {
        return emulatedPositions.values().stream().toList();
    }

    @Override
    public Optional<EmulatedPosition> findBy(InstrumentId instrumentId, ScannerId scannerId) {
        return findAllBy(instrumentId).stream().filter(row -> row.getScannerId().equals(scannerId)).findFirst();
    }

    @Override
    public void saveAll(List<EmulatedPosition> emulatedPositions) {
        emulatedPositions.forEach(this::save);
    }

    @Override
    public void save(EmulatedPosition emulatedPosition) {
        this.emulatedPositions.put(
            emulatedPosition.getId(),
            emulatedPosition
        );
    }
}
