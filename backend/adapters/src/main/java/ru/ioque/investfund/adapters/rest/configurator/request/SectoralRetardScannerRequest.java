package ru.ioque.investfund.adapters.rest.configurator.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.configurator.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.SectoralRetardAlgorithmConfig;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralRetardScannerRequest extends ScannerRequest {
    @NotNull(message = "The historyScale is required.")
    Double historyScale;
    @NotNull(message = "The intradayScale is required.")
    Double intradayScale;

    @Builder
    public SectoralRetardScannerRequest(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        Double historyScale,
        Double intradayScale
    ) {
        super(workPeriodInMinutes, description, datasourceId, tickers);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    @Override
    public AlgorithmConfig buildConfig() {
        return new SectoralRetardAlgorithmConfig(historyScale, intradayScale);
    }
}
