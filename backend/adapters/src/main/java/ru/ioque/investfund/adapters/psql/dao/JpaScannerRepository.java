package ru.ioque.investfund.adapters.psql.dao;

import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.psql.entity.scanner.ScannerEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaScannerRepository extends JpaAbstractRepository<ScannerEntity> {
    List<ScannerEntity> findAllByDatasourceId(UUID datasourceId);
}
