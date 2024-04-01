package ru.ioque.core.dto.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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
}
