package ru.ioque.investfund.domain.scanner.financial.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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
        this.objectIds = objectIds == null ? new ArrayList<>() : new ArrayList<>(objectIds);
    }

    public void updateObjectIds(List<UUID> ids) {
        this.objectIds.clear();
        this.objectIds.addAll(ids);
    }
}
