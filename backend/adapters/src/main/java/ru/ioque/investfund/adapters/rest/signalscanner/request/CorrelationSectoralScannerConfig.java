package ru.ioque.investfund.adapters.rest.signalscanner.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.correlationsectoral.CorrelationSectoralAlgorithmConfigurator;
import ru.ioque.investfund.domain.scanner.entity.algorithms.AlgorithmConfigurator;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CorrelationSectoralScannerConfig extends ScannerConfigRequest {
    @NotNull(message = "The futuresOvernightScale is required.")
    Double futuresOvernightScale;
    @NotNull(message = "The stockOvernightScale is required.")
    Double stockOvernightScale;
    @NotNull(message = "The futuresTicker is required.")
    String futuresTicker;

    @Builder
    public CorrelationSectoralScannerConfig(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> ids,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(workPeriodInMinutes, description, ids);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    @Override
    public AlgorithmConfigurator buildConfig() {
        return new CorrelationSectoralAlgorithmConfigurator(getWorkPeriodInMinutes(), getDescription(), getIds(), futuresOvernightScale, stockOvernightScale, futuresTicker);
    }
}
