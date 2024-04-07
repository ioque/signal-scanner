package ru.ioque.investfund.adapters.rest.signalscanner.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.AlgorithmConfig;
import ru.ioque.investfund.domain.scanner.entity.algorithms.sectoralretard.SectoralRetardAlgorithmConfig;

import java.util.List;

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
        List<String> tickers,
        Double historyScale,
        Double intradayScale
    ) {
        super(workPeriodInMinutes, description, tickers);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    @Override
    public AlgorithmConfig buildConfig() {
        return new SectoralRetardAlgorithmConfig(historyScale, intradayScale);
    }
}
