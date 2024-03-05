package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.domain.scanner.financial.entity.ScannerLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FakeScannerLogRepository implements ScannerLogRepository {
    public Map<UUID, List<ScannerLog>> logs = new ConcurrentHashMap<>();

    @Override
    public List<ScannerLog> getAllBy(UUID scannerId) {
        return logs.get(scannerId);
    }

    @Override
    public void saveAll(UUID id, List<ScannerLog> logs) {
        if (!this.logs.containsKey(id)) {
            this.logs.put(id, new ArrayList<>());
        }
        this.logs.get(id).addAll(logs);
    }
}
