package ru.ioque.investfund.domain.scanner.entity.sectoralretard;

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
public class SectoralRetardSignalConfig extends SignalConfig {
    private final Double historyScale;
    private final Double intradayScale;

    @Builder
    public SectoralRetardSignalConfig(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        Double historyScale,
        Double intradayScale) {
        super(workPeriodInMinutes, description, objectIds);
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
            new SectoralRetardAlgorithm(historyScale, intradayScale),
            lastExecution,
            finInstruments,
            signals
        );
    }
}
