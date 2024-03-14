package ru.ioque.investfund.domain.scanner.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.algorithms.SignalAlgorithm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public abstract class SignalConfig {
    private final List<UUID> objectIds;
    public abstract SignalAlgorithm factorySearchAlgorithm();
    public abstract boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime);

    public SignalConfig(List<UUID> objectIds) {
        if (objectIds == null || objectIds.isEmpty()) {
            throw new DomainException("Не передан список анализируемых инструментов.");
        }
        this.objectIds = new ArrayList<>(objectIds);
    }

    public void updateObjectIds(List<UUID> ids) {
        this.objectIds.clear();
        this.objectIds.addAll(ids);
        if (this.objectIds.isEmpty()) {
            throw new DomainException("Не передан список анализируемых инструментов.");
        }
    }
}
