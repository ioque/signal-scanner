package ru.ioque.investfund.adapters.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.persistence.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.CorrelationSectoralScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SectoralRetardScannerEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalScannerRepository;
import ru.ioque.investfund.adapters.persistence.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.application.adapters.ScannerConfigRepository;
import ru.ioque.investfund.domain.configurator.entity.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.AnomalyVolumeAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.SectoralCorrelationAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.PrefSimpleAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.SectoralRetardAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.ScannerConfig;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class ImplScannerConfigRepository implements ScannerConfigRepository {
    JpaSignalScannerRepository repository;
    JpaInstrumentRepository instruments;

    @Override
    @Transactional
    public void save(ScannerConfig config) {
        Optional<ScannerEntity> scanner = repository.findById(config.getId());
        if (scanner.isPresent()) {
            scanner.get().updateConfig(config);
            repository.save(scanner.get());
            return;
        }
        repository.save(factories.get(config.getAlgorithmConfig().getClass()).apply(config));
    }

    @Override
    @Transactional
    public boolean existsBy(UUID id) {
        return repository.existsById(id);
    }

    static Map<Class<? extends AlgorithmConfig>, Function<ScannerConfig, ScannerEntity>> factories = Map.of(
        AnomalyVolumeAlgorithmConfig.class, AnomalyVolumeScannerEntity::from,
        SectoralCorrelationAlgorithmConfig.class, CorrelationSectoralScannerEntity::from,
        SectoralRetardAlgorithmConfig.class, SectoralRetardScannerEntity::from,
        PrefSimpleAlgorithmConfig.class, PrefSimpleScannerEntity::from
    );
}
