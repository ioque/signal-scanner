package ru.ioque.investfund.domain.scanner.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public abstract class SignalConfig {
    private final Integer workPeriodInMinutes;
    private final String description;
    private final List<UUID> objectIds;
    public abstract SignalScanner factoryScanner(
        UUID id,
        LocalDateTime lastExecution,
        List<FinInstrument> finInstruments,
        List<Signal> signals);

    public SignalScanner factoryScanner(UUID id, List<FinInstrument> finInstruments) {
        return factoryScanner(id, null, finInstruments, new ArrayList<>());
    }

    public SignalConfig(Integer workPeriodInMinutes, String description, List<UUID> objectIds) {
        if (workPeriodInMinutes == null) {
            throw new DomainException("Не передан период работы сканера.");
        }
        if (description == null || description.isBlank()) {
            throw new DomainException("Не передано описание.");
        }
        if (objectIds == null || objectIds.isEmpty()) {
            throw new DomainException("Не передан список анализируемых инструментов.");
        }
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.objectIds = new ArrayList<>(objectIds);
    }
}
