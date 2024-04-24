package ru.ioque.investfund.adapters.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.risk.EmulatedPositionEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaEmulatedPositionRepository;
import ru.ioque.investfund.application.adapters.EmulatedPositionRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class PsqlEmulatedPositionRepository implements EmulatedPositionRepository {
    JpaEmulatedPositionRepository emulatedPositionRepository;

    @Override
    public List<EmulatedPosition> findAllBy(InstrumentId instrumentId) {
        return emulatedPositionRepository
            .findAllByInstrumentId(instrumentId.getUuid())
            .stream()
            .map(EmulatedPositionEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<EmulatedPosition> findBy(InstrumentId instrumentId, ScannerId scannerId) {
        return emulatedPositionRepository
            .findByInstrumentIdAndScannerId(instrumentId.getUuid(), scannerId.getUuid())
            .map(EmulatedPositionEntity::toDomain);
    }

    @Override
    public void saveAll(List<EmulatedPosition> emulatedPositions) {
        emulatedPositionRepository.saveAll(
            emulatedPositions.stream().map(EmulatedPositionEntity::from).toList()
        );
    }

    @Override
    public void save(EmulatedPosition emulatedPosition) {
        emulatedPositionRepository.save(EmulatedPositionEntity.from(emulatedPosition));
    }
}
