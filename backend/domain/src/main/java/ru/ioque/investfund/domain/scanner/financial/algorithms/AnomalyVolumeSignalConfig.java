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
public class AnomalyVolumeSignalConfig implements SignalConfig {
    private final Double scaleCoefficient;
    //Период расчета в исторических данных = 180 дней
    //если данных при запросе нет, надо как-то доинтегрировать? или рассчитывать с тем что есть?
    private final Integer historyPeriod;
    private final String indexTicker;

    public AnomalyVolumeSignalConfig(Double scaleCoefficient, Integer historyPeriod, String indexTicker) {
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
        validate();
    }

    private void validate() {
        if (scaleCoefficient == null) {
            throw new DomainException("Не передан параметр scaleCoefficient.");
        }
        if (historyPeriod == null) {
            throw new DomainException("Не передан параметр historyPeriod.");
        }
        if (indexTicker == null || indexTicker.isEmpty()) {
            throw new DomainException("Не передан параметр indexTicker.");
        }
        if (scaleCoefficient <= 0) {
            throw new DomainException("Параметр scaleCoefficient должен быть больше нуля.");
        }
        if (historyPeriod <= 0) {
            throw new DomainException("Параметр historyPeriod должен быть больше нуля.");
        }
    }

    @Override
    public SignalAlgorithm factorySearchAlgorithm() {
        return new AnomalyVolumeAlgorithm(scaleCoefficient, historyPeriod, indexTicker);
    }

    @Override
    public boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime) {
        return Duration.between(lastExecution, nowDateTime).toMinutes() >= 1;
    }
}
