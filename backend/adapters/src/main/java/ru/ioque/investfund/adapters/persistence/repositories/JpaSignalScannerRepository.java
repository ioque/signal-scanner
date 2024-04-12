package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaSignalScannerRepository extends JpaAbstractRepository<ScannerEntity> {
    List<ScannerEntity> findAllByDatasourceId(UUID datasourceId);
}
