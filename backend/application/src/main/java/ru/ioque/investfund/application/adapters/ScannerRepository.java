package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScannerRepository {
    void save(SignalScanner scanner);
    List<SignalScanner> getAllBy(UUID datasourceId);
    Optional<SignalScanner> getBy(UUID scannerId);
}
