package ru.ioque.investfund.domain.scanner.entity.anomalyvolume;

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
public class AnomalyVolumeSignalConfig extends SignalConfig {
    private final Double scaleCoefficient;
    private final Integer historyPeriod;
    private final String indexTicker;

    @Builder
    public AnomalyVolumeSignalConfig(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(workPeriodInMinutes, description, objectIds);
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
            new AnomalyVolumeAlgorithm(scaleCoefficient, historyPeriod, indexTicker),
            lastExecution,
            finInstruments,
            signals
        );
    }
}
