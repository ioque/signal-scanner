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
import ru.ioque.investfund.domain.configurator.CorrelationSectoralAlgorithmConfig;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CorrelationSectoralScannerRequest extends ScannerRequest {
    @NotNull(message = "The futuresOvernightScale is required.")
    Double futuresOvernightScale;
    @NotNull(message = "The stockOvernightScale is required.")
    Double stockOvernightScale;
    @NotNull(message = "The futuresTicker is required.")
    String futuresTicker;

    @Builder
    public CorrelationSectoralScannerRequest(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(workPeriodInMinutes, description, datasourceId, tickers);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    @Override
    public AlgorithmConfig buildConfig() {
        return new CorrelationSectoralAlgorithmConfig(futuresOvernightScale, stockOvernightScale, futuresTicker);
    }
}
