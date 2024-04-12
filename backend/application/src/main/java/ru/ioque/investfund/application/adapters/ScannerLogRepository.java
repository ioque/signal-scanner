package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.ScannerLog;

import java.util.List;
import java.util.UUID;

public interface ScannerLogRepository {
    List<ScannerLog> getAllBy(UUID scannerId);
    void save(UUID scannerId, ScannerLog scanning);
}
