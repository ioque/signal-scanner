package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.application.adapters.ScannerConfigRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.configurator.SignalScannerConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeScannerRepository implements ScannerRepository, ScannerConfigRepository {
    public Map<UUID, SignalScanner> scannerMap = new HashMap<>();
    FinInstrumentRepository finInstrumentRepository;

    public FakeScannerRepository(FinInstrumentRepository finInstrumentRepository) {
        this.finInstrumentRepository = finInstrumentRepository;
    }

    @Override
    public void save(SignalScanner dataScanner) {
        this.scannerMap.put(dataScanner.getId(), dataScanner);
    }

    @Override
    public List<SignalScanner> getAll() {
        scannerMap.values().stream().map(SignalScanner::getId).forEach(this::updateTradingSnapshots);
        return scannerMap.values().stream().toList();
    }

    @Override
    public void save(SignalScannerConfig config) {
        scannerMap.put(config.getId(), map(config));
    }

    @Override
    public boolean existsBy(UUID id) {
        return scannerMap.containsKey(id);
    }

    private Optional<SignalScanner> getBy(UUID id) {
        return Optional.ofNullable(scannerMap.get(id));
    }

    private void updateTradingSnapshots(UUID id) {
        if (scannerMap.containsKey(id)) {
            SignalScanner scanner = scannerMap.get(id);
            scannerMap.put(id,
                SignalScanner.builder()
                    .id(scanner.getId())
                    .workPeriodInMinutes(scanner.getWorkPeriodInMinutes())
                    .algorithm(scanner.getAlgorithm())
                    .description(scanner.getDescription())
                    .signals(scanner.getSignals())
                    .lastExecutionDateTime(scanner.getLastExecutionDateTime().orElse(null))
                    .tradingSnapshots(finInstrumentRepository.getBy(scanner.getTickers()))
                    .build()
            );
        }
    }

    private SignalScanner map(SignalScannerConfig config) {
        return SignalScanner.builder()
            .id(config.getId())
            .workPeriodInMinutes(config.getWorkPeriodInMinutes())
            .algorithm(config.getAlgorithmConfig().factoryAlgorithm())
            .description(config.getDescription())
            .signals(getBy(config.getId()).map(SignalScanner::getSignals).orElse(new ArrayList<>()))
            .lastExecutionDateTime(getBy(config.getId()).map(SignalScanner::getLastExecutionDateTime).flatMap(r -> r).orElse(null))
            .tradingSnapshots(finInstrumentRepository.getBy(config.getTickers()))
            .build();
    }
}
