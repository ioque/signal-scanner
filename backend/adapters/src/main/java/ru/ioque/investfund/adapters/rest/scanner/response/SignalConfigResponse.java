package ru.ioque.investfund.adapters.rest.scanner.response;

import ru.ioque.investfund.adapters.persistence.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SectoralFuturesScannerEntity;
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
        SectoralFuturesScannerEntity.class, config -> CorrelationSectoralScannerConfigResponse.from((SectoralFuturesScannerEntity) config),
        SectoralRetardScannerEntity.class, config -> SectoralRetardScannerConfigResponse.from((SectoralRetardScannerEntity) config),
        PrefSimpleScannerEntity.class, config -> PrefCommonConfigResponse.from((PrefSimpleScannerEntity) config)
    );
}
