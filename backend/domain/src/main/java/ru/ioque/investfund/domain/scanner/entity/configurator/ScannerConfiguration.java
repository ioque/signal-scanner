package ru.ioque.investfund.domain.scanner.entity.configurator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public abstract class ScannerConfiguration {
    private final Integer workPeriodInMinutes;
    private final String description;
    private final List<UUID> objectIds;
    abstract ScannerAlgorithm factoryAlgorithm();

    public ScannerConfiguration(Integer workPeriodInMinutes, String description, List<UUID> objectIds) {
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
