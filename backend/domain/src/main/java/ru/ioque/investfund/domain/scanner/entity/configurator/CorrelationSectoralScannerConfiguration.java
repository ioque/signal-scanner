package ru.ioque.investfund.domain.scanner.entity.configurator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.algorithms.CorrelationSectoralAlgorithm;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CorrelationSectoralScannerConfiguration extends ScannerConfiguration {
    private final Double futuresOvernightScale;
    private final Double stockOvernightScale;
    private final String futuresTicker;

    @Builder
    public CorrelationSectoralScannerConfiguration(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(workPeriodInMinutes, description, objectIds);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
        validate();
    }

    private void validate() {
        if (futuresOvernightScale == null) {
            throw new DomainException("Не передан параметр futuresOvernightScale.");
        }
        if (stockOvernightScale == null) {
            throw new DomainException("Не передан параметр stockOvernightScale.");
        }
        if (futuresTicker == null || futuresTicker.isEmpty()) {
            throw new DomainException("Не передан параметр futuresTicker.");
        }
        if (futuresOvernightScale <= 0) {
            throw new DomainException("Параметр futuresOvernightScale должен быть больше нуля.");
        }
        if (stockOvernightScale <= 0) {
            throw new DomainException("Параметр stockOvernightScale должен быть больше нуля.");
        }
    }

    @Override
    protected ScannerAlgorithm factoryAlgorithm() {
        return new CorrelationSectoralAlgorithm(
            futuresOvernightScale,
            stockOvernightScale,
            futuresTicker
        );
    }
}
