package ru.ioque.investfund.adapters.storage.jpa.repositories;

import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerEntity;

@Repository
public interface SignalScannerEntityRepository extends AbstractEntityRepository<ScannerEntity> {
}
