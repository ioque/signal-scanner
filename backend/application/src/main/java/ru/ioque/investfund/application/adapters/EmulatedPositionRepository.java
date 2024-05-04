package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPositionId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.Optional;

public interface EmulatedPositionRepository {
    boolean existsOpenPositions();
    List<EmulatedPosition> findAllBy(InstrumentId instrumentId);
    Optional<EmulatedPosition> findBy(InstrumentId instrumentId, ScannerId scannerId);
    void saveAll(List<EmulatedPosition> emulatedPositions);
    void save(EmulatedPosition emulatedPosition);
    EmulatedPositionId nextId();
}
