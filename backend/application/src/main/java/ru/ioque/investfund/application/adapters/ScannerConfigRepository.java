package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.configurator.entity.ScannerConfig;

import java.util.UUID;

public interface ScannerConfigRepository {
    void save(ScannerConfig config);
    boolean existsBy(UUID id);
}
