package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.configurator.SignalScannerConfig;

public interface ScannerConfigRepository {
    void save(SignalScannerConfig config);
}
