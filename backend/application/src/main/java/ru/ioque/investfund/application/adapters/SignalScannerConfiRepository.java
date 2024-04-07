package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.SignalScannerConfig;

public interface SignalScannerConfiRepository {
    void save(SignalScannerConfig config);
}
