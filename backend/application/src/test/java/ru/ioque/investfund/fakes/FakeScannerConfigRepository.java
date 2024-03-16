package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ScannerConfigRepository;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeScannerConfigRepository implements ScannerConfigRepository {
    Map<UUID, SignalConfig> configMap = new HashMap<>();
    FakeScannerRepository scannerRepository;

    public FakeScannerConfigRepository(FakeScannerRepository scannerRepository) {
        this.scannerRepository = scannerRepository;
    }

    @Override
    public void save(UUID scannerId, SignalConfig config) {
        configMap.put(scannerId, config);
        if (scannerRepository.scannerMap.containsKey(scannerId)) {
            scannerRepository.scannerMap.put(
                scannerId,
                config.factoryScanner(
                    scannerId,
                    scannerRepository.scannerMap.get(scannerId).getLastExecutionDateTime().orElse(null),
                    scannerRepository.getAllByInstrumentIdIn(config.getObjectIds()),
                    scannerRepository.scannerMap.get(scannerId).getSignals()
                )
            );
        } else {
            scannerRepository.scannerMap.put(
                scannerId,
                config.factoryScanner(
                    scannerId,
                    null,
                    scannerRepository.getAllByInstrumentIdIn(config.getObjectIds()),
                    new ArrayList<>()
                )
            );
        }
    }

    @Override
    public Optional<SignalConfig> getBy(UUID scannerId) {
        return Optional.ofNullable(configMap.get(scannerId));
    }
}
