package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ScannerLogRepository;

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
    public void save(UUID id, ScannerLog log) {
        if (!this.logs.containsKey(id)) {
            this.logs.put(id, new ArrayList<>());
        }
        this.logs.get(id).add(log);
    }
}
