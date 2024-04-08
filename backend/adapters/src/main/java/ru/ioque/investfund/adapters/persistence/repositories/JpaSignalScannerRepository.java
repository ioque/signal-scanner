package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;

@Repository
public interface JpaSignalScannerRepository extends JpaAbstractRepository<ScannerEntity> {
}
