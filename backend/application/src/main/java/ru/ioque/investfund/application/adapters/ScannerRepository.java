package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScannerRepository {
    void save(SignalScanner scanner);
    List<SignalScanner> findAll();
    Optional<SignalScanner> findBy(UUID scannerId);
    SignalScanner getBy(UUID scannerId) throws EntityNotFoundException;
}
