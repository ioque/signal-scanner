package ru.ioque.investfund.domain.scanner.entity.prefsimplepair;

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
public class PrefSimpleSignalConfig extends SignalConfig {
    private final Double spreadParam;

    @Builder
    public PrefSimpleSignalConfig(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        Double spreadParam) {
        super(workPeriodInMinutes, description, objectIds);
        this.spreadParam = spreadParam;
        validate();
    }

    private void validate() {
        if (spreadParam == null) {
            throw new DomainException("Не передан параметр spreadParam.");
        }
        if (spreadParam <= 0) {
            throw new DomainException("Параметр spreadParam должен быть больше нуля.");
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
            new PrefSimpleAlgorithm(spreadParam),
            lastExecution,
            finInstruments,
            signals
        );
    }
}
