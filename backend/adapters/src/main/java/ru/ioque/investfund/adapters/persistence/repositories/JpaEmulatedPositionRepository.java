package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.risk.EmulatedPositionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaEmulatedPositionRepository extends JpaAbstractRepository<EmulatedPositionEntity> {
    List<EmulatedPositionEntity> findAllByInstrumentId(UUID instrumentId);
    Optional<EmulatedPositionEntity> findByInstrumentIdAndScannerId(UUID instrumentId, UUID scannerId);
}
