package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.CorrelationSectoralScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SectoralRetardScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.scanner.entity.AnomalyVolumeAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.CorrelationSectoralAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.PrefSimpleAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.SectoralRetardAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JpaScannerRepo implements ScannerRepository {
    SignalScannerEntityRepository signalScannerEntityRepository;
    FinInstrumentRepository finInstrumentRepository;

    @Transactional(readOnly = true)
    public Optional<SignalScanner> getBy(UUID id) {
        return signalScannerEntityRepository
            .findById(id)
            .map(row -> row.toDomain(finInstrumentRepository.getBy(row.getTickers())));
    }

    @Override
    @Transactional
    public void save(SignalScanner dataScanner) {
        signalScannerEntityRepository.save(toEntity(dataScanner));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SignalScanner> getAll() {
        return signalScannerEntityRepository
            .findAll()
            .stream()
            .map(row -> row.toDomain(finInstrumentRepository.getBy(row.getTickers())))
            .toList();
    }

    private ScannerEntity toEntity(SignalScanner dataScanner) {
        return mappers.get(dataScanner.getAlgorithm().getClass()).apply(dataScanner);
    }

    Map<Class<? extends ScannerAlgorithm>, Function<SignalScanner, ScannerEntity>> mappers = Map.of(
        AnomalyVolumeAlgorithm.class, AnomalyVolumeScannerEntity::from,
        SectoralRetardAlgorithm.class, SectoralRetardScannerEntity::from,
        CorrelationSectoralAlgorithm.class, CorrelationSectoralScannerEntity::from,
        PrefSimpleAlgorithm.class, PrefSimpleScannerEntity::from
    );
}
