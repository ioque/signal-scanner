package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.configurator.SignalScannerConfig;

public interface SignalScannerConfiRepository {
    void save(SignalScannerConfig config);
}
