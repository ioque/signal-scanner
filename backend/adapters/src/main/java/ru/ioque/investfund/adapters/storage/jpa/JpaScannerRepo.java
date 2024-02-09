package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.CorrelationSectoralScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SectoralRetardScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.domain.scanner.financial.algorithms.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.algorithms.CorrelationSectoralSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.algorithms.PrefSimpleSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.algorithms.SectoralRetardSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

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

    @Override
    @Transactional(readOnly = true)
    public Optional<SignalScannerBot> getBy(UUID id) {
        return signalScannerEntityRepository.findById(id).map(SignalScannerEntity::toDomain);
    }

    @Override
    @Transactional
    public void save(SignalScannerBot dataScanner) {
        signalScannerEntityRepository.save(toEntity(dataScanner));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SignalScannerBot> getAll() {
        return signalScannerEntityRepository.findAll().stream().map(SignalScannerEntity::toDomain).toList();
    }

    private SignalScannerEntity toEntity(SignalScannerBot dataScanner) {
        return mappers.get(dataScanner.getConfig().getClass()).apply(dataScanner);
    }

    Map<Class<? extends SignalConfig>, Function<SignalScannerBot, SignalScannerEntity>> mappers = Map.of(
        AnomalyVolumeSignalConfig.class, AnomalyVolumeScannerEntity::from,
        SectoralRetardSignalConfig.class, SectoralRetardScannerEntity::from,
        CorrelationSectoralSignalConfig.class, CorrelationSectoralScannerEntity::from,
        PrefSimpleSignalConfig.class, PrefSimpleScannerEntity::from
    );
}
