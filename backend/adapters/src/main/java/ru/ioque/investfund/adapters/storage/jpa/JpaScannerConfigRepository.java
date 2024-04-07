package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.CorrelationSectoralScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SectoralRetardScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
import ru.ioque.investfund.application.adapters.ScannerConfigRepository;
import ru.ioque.investfund.domain.configurator.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.AnomalyVolumeAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.CorrelationSectoralAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.PrefSimpleAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.SectoralRetardAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.SignalScannerConfig;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class JpaScannerConfigRepository implements ScannerConfigRepository {
    SignalScannerEntityRepository repository;

    @Override
    public void save(SignalScannerConfig config) {
        Optional<ScannerEntity> scanner = repository.findById(config.getId());
        if (scanner.isPresent()) {
            repository.save(updateConfig(scanner.get(), config));
            return;
        }
        repository.save(factories.get(config.getAlgorithmConfig().getClass()).apply(config));
    }

    @Override
    public boolean existsBy(UUID id) {
        return repository.existsById(id);
    }

    private ScannerEntity updateConfig(ScannerEntity scannerEntity, SignalScannerConfig config) {
        scannerEntity.updateConfig(config);
        return scannerEntity;
    }

    static Map<Class<? extends AlgorithmConfig>, Function<SignalScannerConfig, ScannerEntity>> factories = Map.of(
        AnomalyVolumeAlgorithmConfig.class, AnomalyVolumeScannerEntity::from,
        CorrelationSectoralAlgorithmConfig.class, CorrelationSectoralScannerEntity::from,
        SectoralRetardAlgorithmConfig.class, SectoralRetardScannerEntity::from,
        PrefSimpleAlgorithmConfig.class, PrefSimpleScannerEntity::from
    );
}
