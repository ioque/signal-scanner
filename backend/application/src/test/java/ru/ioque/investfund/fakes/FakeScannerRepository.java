package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeScannerRepository implements ScannerRepository {
    public Map<UUID, SignalScanner> scannerMap = new HashMap<>();
    FinInstrumentRepository finInstrumentRepository;

    public FakeScannerRepository(FinInstrumentRepository finInstrumentRepository) {
        this.finInstrumentRepository = finInstrumentRepository;
    }

    @Override
    public Optional<SignalScanner> getBy(UUID id) {
        return Optional.ofNullable(scannerMap.get(id)).map(this::map);
    }

    private SignalScanner map(SignalScanner signalScanner) {
        return SignalScanner.builder()
            .id(signalScanner.getId())
            .workPeriodInMinutes(signalScanner.getWorkPeriodInMinutes())
            .algorithm(signalScanner.getAlgorithm())
            .description(signalScanner.getDescription())
            .signals(signalScanner.getSignals())
            .lastExecutionDateTime(signalScanner.getLastExecutionDateTime().orElse(null))
            .finInstruments(finInstrumentRepository.getBy(signalScanner.getTickers()))
            .build();
    }

    @Override
    public void save(SignalScanner dataScanner) {
        this.scannerMap.put(dataScanner.getId(), dataScanner);
    }

    @Override
    public List<SignalScanner> getAll() {
        return scannerMap.values().stream().map(this::map).toList();
    }
}
