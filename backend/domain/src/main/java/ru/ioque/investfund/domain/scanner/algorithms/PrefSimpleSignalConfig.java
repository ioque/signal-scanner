package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PrefSimpleSignalConfig extends SignalConfig {
    private final Double spreadParam;

    @Builder
    public PrefSimpleSignalConfig(List<UUID> objectIds, Double spreadParam) {
        super(objectIds);
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
    public SignalAlgorithm factorySearchAlgorithm() {
        return new PrefSimpleAlgorithm(spreadParam);
    }

    @Override
    public boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime) {
        return Duration.between(lastExecution, nowDateTime).toMinutes() >= 1;
    }
}
