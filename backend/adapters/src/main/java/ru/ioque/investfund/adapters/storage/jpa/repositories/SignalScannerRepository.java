package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalScannerEntity;

@Repository
public interface SignalScannerRepository extends AbstractEntityRepository<SignalScannerEntity> {
}
