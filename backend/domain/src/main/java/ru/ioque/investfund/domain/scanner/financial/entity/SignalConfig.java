package ru.ioque.investfund.domain.scanner.financial.entity;

import java.time.LocalDateTime;

public interface SignalConfig {
    SignalAlgorithm factorySearchAlgorithm();
    boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime);
}
