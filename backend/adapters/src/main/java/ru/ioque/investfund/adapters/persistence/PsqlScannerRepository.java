package ru.ioque.investfund.adapters.persistence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.persistence.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SectoralFuturesScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SectoralRetardScannerEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalScannerRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.algorithms.AlgorithmType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PsqlScannerRepository implements ScannerRepository {
    JpaSignalScannerRepository signalScannerEntityRepository;
    DateTimeProvider dateTimeProvider;

    @Transactional(readOnly = true)
    public Optional<SignalScanner> getBy(UUID id) {
        return signalScannerEntityRepository
            .findById(id)
            .map(ScannerEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SignalScanner> getAllBy(UUID datasourceId) {
        return signalScannerEntityRepository
            .findAllByDatasourceId(datasourceId)
            .stream()
            .map(ScannerEntity::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void save(SignalScanner dataScanner) {
        signalScannerEntityRepository.save(toEntity(dataScanner));
    }


    private ScannerEntity toEntity(SignalScanner dataScanner) {
        return mappers.get(dataScanner.getProperties().getType()).apply(dataScanner);
    }

    Map<AlgorithmType, Function<SignalScanner, ScannerEntity>> mappers = Map.of(
        AlgorithmType.ANOMALY_VOLUME, AnomalyVolumeScannerEntity::from,
        AlgorithmType.SECTORAL_RETARD, SectoralRetardScannerEntity::from,
        AlgorithmType.SECTORAL_FUTURES, SectoralFuturesScannerEntity::from,
        AlgorithmType.PREF_SIMPLE, PrefSimpleScannerEntity::from
    );
}
