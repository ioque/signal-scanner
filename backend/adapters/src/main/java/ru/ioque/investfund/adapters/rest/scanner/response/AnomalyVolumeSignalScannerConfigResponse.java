package ru.ioque.investfund.adapters.rest.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.AnomalyVolumeScannerEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnomalyVolumeSignalScannerConfigResponse extends SignalConfigResponse {
    Double scaleCoefficient;
    Integer historyPeriod;
    String indexTicker;

    public static SignalConfigResponse from(AnomalyVolumeScannerEntity scanner) {
        return new AnomalyVolumeSignalScannerConfigResponse(
            scanner.getScaleCoefficient(),
            scanner.getHistoryPeriod(),
            scanner.getIndexTicker()
        );
    }
}
