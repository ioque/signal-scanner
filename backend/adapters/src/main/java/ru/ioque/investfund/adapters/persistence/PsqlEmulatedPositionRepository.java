package ru.ioque.investfund.adapters.persistence;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.repository.EmulatedPositionRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.position.Position;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;

@Component
public class PsqlEmulatedPositionRepository implements EmulatedPositionRepository {
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
}
