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
import ru.ioque.investfund.domain.configurator.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.AnomalyVolumeAlgorithmConfig;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnomalyVolumeScannerRequest extends ScannerRequest {
    @NotNull(message = "The scaleCoefficient is required.")
    Double scaleCoefficient;
    @NotNull(message = "The historyPeriod is required.")
    Integer historyPeriod;
    @NotBlank(message = "The indexTicker is required.")
    String indexTicker;

    @Builder
    public AnomalyVolumeScannerRequest(
        Integer workPeriodInMinutes,
        String description,
        List<String> tickers,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(workPeriodInMinutes, description, tickers);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    @Override
    public AlgorithmConfig buildConfig() {
        return new AnomalyVolumeAlgorithmConfig(scaleCoefficient, historyPeriod, indexTicker);
    }
}
