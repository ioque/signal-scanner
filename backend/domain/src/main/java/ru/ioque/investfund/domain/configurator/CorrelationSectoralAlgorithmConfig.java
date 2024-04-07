package ru.ioque.investfund.domain.configurator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
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
        validate();
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new CorrelationSectoralAlgorithm(
            futuresOvernightScale,
            stockOvernightScale,
            futuresTicker
        );
    }

    private void validate() {
        if (getFuturesOvernightScale() == null) {
            throw new DomainException("Не передан параметр futuresOvernightScale.");
        }
        if (getStockOvernightScale() == null) {
            throw new DomainException("Не передан параметр stockOvernightScale.");
        }
        if (getFuturesTicker() == null || getFuturesTicker().isEmpty()) {
            throw new DomainException("Не передан параметр futuresTicker.");
        }
        if (getFuturesOvernightScale() <= 0) {
            throw new DomainException("Параметр futuresOvernightScale должен быть больше нуля.");
        }
        if (getStockOvernightScale() <= 0) {
            throw new DomainException("Параметр stockOvernightScale должен быть больше нуля.");
        }
    }
}
