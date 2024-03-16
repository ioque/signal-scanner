package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.SignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScannerRepository {
    Optional<SignalScanner> getBy(UUID id);
    void saveConfig(UUID id, SignalConfig config);
    void save(SignalScanner scanner);
    List<SignalScanner> getAll();
}
