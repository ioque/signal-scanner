package ru.ioque.investfund.domain.scanner.entity.correlationsectoral;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CorrelationSectoralSignalConfig extends SignalConfig {
    private final Double futuresOvernightScale;
    private final Double stockOvernightScale;
    private final String futuresTicker;

    @Builder
    public CorrelationSectoralSignalConfig(
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
    public SignalScanner factoryScanner(
        UUID id,
        LocalDateTime lastExecution,
        List<FinInstrument> finInstruments,
        List<Signal> signals
    ) {
        return new SignalScanner(
            id,
            getWorkPeriodInMinutes(),
            getDescription(),
            new CorrelationSectoralAlgorithm(
                futuresOvernightScale,
                stockOvernightScale,
                futuresTicker
            ),
            lastExecution,
            finInstruments,
            signals
        );
    }
}
