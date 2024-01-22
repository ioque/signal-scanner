package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeScannerRepository implements ScannerRepository {
    Map<UUID, SignalScannerBot> financialDataScannerMap = new HashMap<>();
    ExchangeRepository exchangeRepository;

    public FakeScannerRepository(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    @Override
    public Optional<SignalScannerBot> getBy(UUID id) {
        return Optional.ofNullable(financialDataScannerMap.get(id));
    }

    @Override
    public void save(SignalScannerBot dataScanner) {
        this.financialDataScannerMap.put(dataScanner.getId(), dataScanner);
    }

    @Override
    public List<SignalScannerBot> getAll() {
        return financialDataScannerMap.values().stream().toList();
    }
}
