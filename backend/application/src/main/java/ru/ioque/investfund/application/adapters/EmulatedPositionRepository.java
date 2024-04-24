package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;

import java.util.List;

public interface EmulatedPositionRepository {
    List<EmulatedPosition> findAllBy(InstrumentId instrumentId);
    void saveAll(List<EmulatedPosition> emulatedPositions);
}
