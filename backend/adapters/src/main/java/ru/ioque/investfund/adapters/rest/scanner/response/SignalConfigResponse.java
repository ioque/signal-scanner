package ru.ioque.investfund.adapters.rest.scanner.response;

import ru.ioque.investfund.adapters.psql.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.psql.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.psql.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.psql.entity.scanner.SectoralFuturesScannerEntity;
import ru.ioque.investfund.adapters.psql.entity.scanner.SectoralRetardScannerEntity;

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
