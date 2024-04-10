package ru.ioque.investfund.adapters.rest.configurator.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.configurator.command.SaveAnomalyVolumeScanner;
import ru.ioque.investfund.domain.configurator.command.SaveScannerCommand;

import java.util.List;
import java.util.UUID;

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
        UUID datasourceId,
        List<String> tickers,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(workPeriodInMinutes, description, datasourceId, tickers);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    @Override
    public SaveScannerCommand toCommand() {
        return SaveAnomalyVolumeScanner.builder()
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .datasourceId(getDatasourceId())
            .tickers(getTickers())
            .scaleCoefficient(getScaleCoefficient())
            .historyPeriod(getHistoryPeriod())
            .indexTicker(getIndexTicker())
            .build();
    }
}
