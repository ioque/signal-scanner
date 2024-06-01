package ru.ioque.investfund.application.adapters.repository;

import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.position.Position;
import ru.ioque.investfund.domain.position.PositionId;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;

import java.util.List;
import java.util.Optional;

public interface EmulatedPositionRepository {
    void save(Position position);
    List<Position> findAllBy(InstrumentId instrumentId);
    Optional<Position> findActualBy(InstrumentId instrumentId, ScannerId scannerId);
    PositionId nextId();
}
