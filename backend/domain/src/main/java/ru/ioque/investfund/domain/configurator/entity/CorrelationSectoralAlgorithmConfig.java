package ru.ioque.investfund.domain.configurator.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.CorrelationSectoralAlgorithm;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
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
        setFuturesOvernightScale(futuresOvernightScale);
        setStockOvernightScale(stockOvernightScale);
        setFuturesTicker(futuresTicker);
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new CorrelationSectoralAlgorithm(
            futuresOvernightScale,
            stockOvernightScale,
            futuresTicker
        );
    }

    private void setFuturesOvernightScale(Double futuresOvernightScale) {
        if (futuresOvernightScale == null) {
            throw new DomainException("Не передан параметр futuresOvernightScale.");
        }
        if (futuresOvernightScale <= 0) {
            throw new DomainException("Параметр futuresOvernightScale должен быть больше нуля.");
        }
        this.futuresOvernightScale = futuresOvernightScale;
    }

    private void setStockOvernightScale(Double stockOvernightScale) {
        if (stockOvernightScale == null) {
            throw new DomainException("Не передан параметр stockOvernightScale.");
        }
        if (stockOvernightScale <= 0) {
            throw new DomainException("Параметр stockOvernightScale должен быть больше нуля.");
        }
        this.stockOvernightScale = stockOvernightScale;
    }

    private void setFuturesTicker(String futuresTicker) {
        if (futuresTicker == null || futuresTicker.isEmpty()) {
            throw new DomainException("Не передан параметр futuresTicker.");
        }
        this.futuresTicker = futuresTicker;
    }
}
