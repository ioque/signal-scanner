package ru.ioque.investfund.adapters.rest.signalscanner.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.anomalyvolume.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnomalyVolumeScannerConfig extends ScannerConfigRequest {
    @NotNull(message = "The scaleCoefficient is required.")
    Double scaleCoefficient;
    @NotNull(message = "The historyPeriod is required.")
    Integer historyPeriod;
    @NotBlank(message = "The indexTicker is required.")
    String indexTicker;

    @Builder
    public AnomalyVolumeScannerConfig(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> ids,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(workPeriodInMinutes, description, ids);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    @Override
    public SignalConfig buildConfig() {
        return new AnomalyVolumeSignalConfig(getWorkPeriodInMinutes(), getDescription(), getIds(), scaleCoefficient, historyPeriod, indexTicker);
    }
}
