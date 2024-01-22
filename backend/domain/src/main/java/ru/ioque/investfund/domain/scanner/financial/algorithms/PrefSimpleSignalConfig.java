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
public class PrefSimpleSignalConfig implements SignalConfig {
    private final Double spreadParam;

    public PrefSimpleSignalConfig(Double spreadParam) {
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
