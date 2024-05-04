package ru.ioque.investfund.adapters.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.persistence.entity.risk.EmulatedPositionEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaEmulatedPositionRepository;
import ru.ioque.investfund.application.adapters.EmulatedPositionRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPositionId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class PsqlEmulatedPositionRepository implements EmulatedPositionRepository {
    JpaEmulatedPositionRepository emulatedPositionRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean existsOpenPositions() {
        return emulatedPositionRepository.findAll().stream().anyMatch(EmulatedPositionEntity::getIsOpen);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmulatedPosition> findAllBy(InstrumentId instrumentId) {
        return emulatedPositionRepository
            .findAllByInstrumentId(instrumentId.getUuid())
            .stream()
            .map(EmulatedPositionEntity::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmulatedPosition> findBy(InstrumentId instrumentId, ScannerId scannerId) {
        return emulatedPositionRepository
            .findByInstrumentIdAndScannerId(instrumentId.getUuid(), scannerId.getUuid())
            .map(EmulatedPositionEntity::toDomain);
    }

    @Override
    @Transactional
    public void saveAll(List<EmulatedPosition> emulatedPositions) {
        emulatedPositionRepository.saveAll(
            emulatedPositions.stream().map(EmulatedPositionEntity::from).toList()
        );
    }

    @Override
    @Transactional
    public void save(EmulatedPosition emulatedPosition) {
        emulatedPositionRepository.save(EmulatedPositionEntity.from(emulatedPosition));
    }

    @Override
    public EmulatedPositionId nextId() {
        return EmulatedPositionId.from(UUID.randomUUID());
    }
}
