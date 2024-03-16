package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

import java.util.Optional;
import java.util.UUID;

public interface ScannerConfigRepository {
    void save(UUID scannerId, SignalConfig config);
    Optional<SignalConfig> getBy(UUID scannerId);
}
