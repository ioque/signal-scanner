package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.financial.entity.ScannerLog;

import java.util.List;
import java.util.UUID;

public interface ScannerLogRepository {
    List<ScannerLog> getAllBy(UUID scannerId);
    void saveAll(UUID scannerId, List<ScannerLog> scanning);
}
