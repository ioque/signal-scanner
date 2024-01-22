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
public class SectoralRetardSignalConfig implements SignalConfig {
    private final Double historyScale;
    private final Double intradayScale;

    public SectoralRetardSignalConfig(Double historyScale, Double intradayScale) {
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
        validate();
    }

    private void validate() {
        if (historyScale == null) {
            throw new DomainException("Не передан параметр historyScale.");
        }
        if (historyScale <= 0) {
            throw new DomainException("Параметр historyScale должен быть больше нуля.");
        }
        if (intradayScale == null) {
            throw new DomainException("Не передан параметр intradayScale.");
        }
        if (intradayScale <= 0) {
            throw new DomainException("Параметр intradayScale должен быть больше нуля.");
        }
    }

    @Override
    public SignalAlgorithm factorySearchAlgorithm() {
        return new SectoralRetardAlgorithm(historyScale, intradayScale);
    }

    @Override
    public boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime) {
        return Duration.between(lastExecution, nowDateTime).toHours() >= 1;
    }
}
