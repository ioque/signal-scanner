package ru.ioque.investfund.application.adapters.journal;

import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPositionId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.Optional;

public interface EmulatedPositionJournal {
    void publish(EmulatedPosition emulatedPosition);
    List<EmulatedPosition> findAllBy(InstrumentId instrumentId);
    Optional<EmulatedPosition> findActualBy(InstrumentId instrumentId, ScannerId scannerId);
    EmulatedPositionId nextId();
}
