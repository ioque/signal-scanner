package ru.ioque.investfund.adapters.rest.signalscanner.response;

import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.CorrelationSectoralScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SectoralRetardScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalScannerEntity;

import java.util.Map;
import java.util.function.Function;

public abstract class SignalConfigResponse {
    public static SignalConfigResponse from(SignalScannerEntity scanner) {
        return mappers.get(scanner.getClass()).apply(scanner);
    }

    private static Map<Class<? extends SignalScannerEntity>, Function<SignalScannerEntity, SignalConfigResponse>> mappers = Map.of(
        AnomalyVolumeScannerEntity.class, config -> AnomalyVolumeSignalScannerConfigResponse.from((AnomalyVolumeScannerEntity) config),
        CorrelationSectoralScannerEntity.class, config -> CorrelationSectoralScannerConfigResponse.from((CorrelationSectoralScannerEntity) config),
        SectoralRetardScannerEntity.class, config -> SectoralRetardScannerConfigResponse.from((SectoralRetardScannerEntity) config),
        PrefSimpleScannerEntity.class, config -> PrefSimpleConfigResponse.from((PrefSimpleScannerEntity) config)
    );
}
