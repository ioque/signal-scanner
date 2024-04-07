package ru.ioque.investfund.adapters.rest.signalscanner.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.AlgorithmConfigurator;
import ru.ioque.investfund.domain.scanner.entity.algorithms.correlationsectoral.CorrelationSectoralAlgorithmConfigurator;

import java.util.List;

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
        List<String> tickers,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(workPeriodInMinutes, description, tickers);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    @Override
    public AlgorithmConfigurator buildConfig() {
        return new CorrelationSectoralAlgorithmConfigurator(futuresOvernightScale, stockOvernightScale, futuresTicker);
    }
}
