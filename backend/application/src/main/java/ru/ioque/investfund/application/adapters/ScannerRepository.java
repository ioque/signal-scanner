package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.SignalScannerBot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScannerRepository {
    Optional<SignalScannerBot> getBy(UUID id);
    void save(SignalScannerBot dataScanner);
    List<SignalScannerBot> getAll();
}
