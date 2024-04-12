package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeScannerRepository implements ScannerRepository {
    public Map<UUID, SignalScanner> scannerMap = new HashMap<>();

    @Override
    public void save(SignalScanner dataScanner) {
        this.scannerMap.put(dataScanner.getId(), dataScanner);
    }

    @Override
    public List<SignalScanner> getAll() {
        return scannerMap.values().stream().toList();
    }

    @Override
    public Optional<SignalScanner> getBy(UUID scannerId) {
        return Optional.ofNullable(scannerMap.get(scannerId));
    }
}
