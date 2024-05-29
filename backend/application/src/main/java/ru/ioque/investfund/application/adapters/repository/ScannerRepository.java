package ru.ioque.investfund.application.adapters.repository;

import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;
import java.util.Optional;

public interface ScannerRepository {
    void save(SignalScanner scanner);
    List<SignalScanner> findAllBy(DatasourceId datasourceId);
    Optional<SignalScanner> findBy(ScannerId scannerId);
    SignalScanner getBy(ScannerId scannerId) throws EntityNotFoundException;
    ScannerId nextId();
    void removeBy(ScannerId scannerId);
}
