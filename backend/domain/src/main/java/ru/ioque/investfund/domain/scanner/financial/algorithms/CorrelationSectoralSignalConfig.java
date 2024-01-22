package ru.ioque.investfund.domain.scanner.financial.algorithms;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalAlgorithm;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalConfig;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CorrelationSectoralSignalConfig implements SignalConfig {
    private final Double futuresOvernightScale;
    private final Double stockOvernightScale;
    private final String futuresTicker;

    public CorrelationSectoralSignalConfig(
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
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
    public SignalAlgorithm factorySearchAlgorithm() {
        return new CorrelationSectoralAlgorithm(futuresOvernightScale, stockOvernightScale, futuresTicker);
    }

    @Override
    public boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime) {
        return Duration.between(lastExecution, nowDateTime).toHours() >= 24;
    }
}
