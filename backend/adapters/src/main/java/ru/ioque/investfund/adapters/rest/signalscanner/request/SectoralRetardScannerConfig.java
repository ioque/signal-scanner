package ru.ioque.investfund.adapters.rest.signalscanner.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.sectoralretard.SectoralRetardAlgorithmConfigurator;
import ru.ioque.investfund.domain.scanner.entity.algorithms.AlgorithmConfigurator;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralRetardScannerConfig extends ScannerConfigRequest {
    @NotNull(message = "The historyScale is required.")
    Double historyScale;
    @NotNull(message = "The intradayScale is required.")
    Double intradayScale;

    @Builder
    public SectoralRetardScannerConfig(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> ids,
        Double historyScale,
        Double intradayScale
    ) {
        super(workPeriodInMinutes, description, ids);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    @Override
    public AlgorithmConfigurator buildConfig() {
        return new SectoralRetardAlgorithmConfigurator(getWorkPeriodInMinutes(), getDescription(), getIds(), historyScale, intradayScale);
    }
}
