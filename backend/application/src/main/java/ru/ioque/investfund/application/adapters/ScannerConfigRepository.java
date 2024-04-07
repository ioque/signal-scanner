package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.configurator.SignalScannerConfig;

import java.util.UUID;

public interface ScannerConfigRepository {
    void save(SignalScannerConfig config);
    boolean existsBy(UUID id);
}
