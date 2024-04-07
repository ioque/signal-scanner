package ru.ioque.investfund.domain.configurator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.algorithms.correlationsectoral.CorrelationSectoralAlgorithm;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CorrelationSectoralAlgorithmConfig extends AlgorithmConfig {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;

    @Builder
    public CorrelationSectoralAlgorithmConfig(
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new CorrelationSectoralAlgorithm(
            futuresOvernightScale,
            stockOvernightScale,
            futuresTicker
        );
    }
}
