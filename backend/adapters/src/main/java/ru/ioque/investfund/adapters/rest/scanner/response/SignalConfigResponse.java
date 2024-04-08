package ru.ioque.investfund.adapters.rest.scanner.response;

import ru.ioque.investfund.adapters.persistence.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.CorrelationSectoralScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SectoralRetardScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;

import java.util.Map;
import java.util.function.Function;

public abstract class SignalConfigResponse {
    public static SignalConfigResponse from(ScannerEntity scanner) {
        return mappers.get(scanner.getClass()).apply(scanner);
    }

    private static final Map<Class<? extends ScannerEntity>, Function<ScannerEntity, SignalConfigResponse>> mappers = Map.of(
        AnomalyVolumeScannerEntity.class, config -> AnomalyVolumeSignalScannerConfigResponse.from((AnomalyVolumeScannerEntity) config),
        CorrelationSectoralScannerEntity.class, config -> CorrelationSectoralScannerConfigResponse.from((CorrelationSectoralScannerEntity) config),
        SectoralRetardScannerEntity.class, config -> SectoralRetardScannerConfigResponse.from((SectoralRetardScannerEntity) config),
        PrefSimpleScannerEntity.class, config -> PrefSimpleConfigResponse.from((PrefSimpleScannerEntity) config)
    );
}
