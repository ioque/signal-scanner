package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.EmulatedPositionRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPositionId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeEmulatedPositionRepository implements EmulatedPositionRepository {
    Map<EmulatedPositionId, EmulatedPosition> emulatedPositions = new ConcurrentHashMap<>();

    @Override
    public List<EmulatedPosition> findAllBy(InstrumentId instrumentId) {
        return emulatedPositions.values().stream().toList();
    }

    @Override
    public void saveAll(List<EmulatedPosition> emulatedPositions) {
        emulatedPositions.forEach(emulatedPosition -> this.emulatedPositions.put(
            emulatedPosition.getId(),
            emulatedPosition
        ));
    }
}
