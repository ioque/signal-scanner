package ru.ioque.investfund.domain.scanner.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.value.ScannerLog;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalScanner extends Domain {
    Integer workPeriodInMinutes;
    String description;
    UUID datasourceId;
    ScannerAlgorithm algorithm;
    @NonFinal
    LocalDateTime lastExecutionDateTime;
    List<Signal> signals;
    List<TradingSnapshot> tradingSnapshots;

    @Builder
    public SignalScanner(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        ScannerAlgorithm algorithm,
        LocalDateTime lastExecutionDateTime,
        List<TradingSnapshot> tradingSnapshots,
        List<Signal> signals
    ) {
        super(id);
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.algorithm = algorithm;
        this.datasourceId = datasourceId;
        this.description = description;
        this.lastExecutionDateTime = lastExecutionDateTime;
        this.signals = signals != null ? new ArrayList<>(signals) : new ArrayList<>();
        this.tradingSnapshots = tradingSnapshots != null ? new ArrayList<>(tradingSnapshots) : new ArrayList<>();
    }

    public Optional<LocalDateTime> getLastExecutionDateTime() {
        return Optional.ofNullable(lastExecutionDateTime);
    }

    public List<ScannerLog> scanning(LocalDateTime dateTimeNow) {
        if (tradingSnapshots.isEmpty()) {
            throw new DomainException("Нет статистических данных для выбранных инструментов.");
        }
        return processResult(algorithm.run(getId(), tradingSnapshots, dateTimeNow));
    }

    private List<ScannerLog> processResult(ScanningResult scanningResult) {
        List<Signal> oldSignals = List.copyOf(this.signals);
        List<Signal> newSignals = List.copyOf(scanningResult.getSignals());
        List<Signal> finalSignalList = new ArrayList<>(newSignals.stream().filter(Signal::isBuy).toList());
        newSignals
            .stream()
            .filter(newSignal -> !newSignal.isBuy())
            .forEach(newSignal -> {
                    if (oldSignals.stream().anyMatch(oldSignal -> newSignal.sameByTicker(oldSignal)
                        && oldSignal.isBuy())) {
                        finalSignalList.add(newSignal);
                    }
                }
            );
        oldSignals.forEach(oldSignal -> {
            if (newSignals.stream().noneMatch(newSignal -> newSignal.sameByTicker(oldSignal))) {
                finalSignalList.add(oldSignal);
            }
        });
        signals.clear();
        signals.addAll(finalSignalList);
        lastExecutionDateTime = scanningResult.getDateTime();
        return scanningResult.getLogs();
    }

    public boolean isTimeForExecution(LocalDateTime nowDateTime) {
        if (getLastExecutionDateTime().isEmpty()) return true;
        return isTimeForExecution(getLastExecutionDateTime().get(), nowDateTime);
    }

    private boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime) {
        return Duration.between(lastExecution, nowDateTime).toMinutes() >= workPeriodInMinutes;
    }

    public List<TradingSnapshot> getTradingSnapshots() {
        return List.copyOf(tradingSnapshots);
    }

    public List<String> getTickers() {
        return List.copyOf(tradingSnapshots.stream().map(TradingSnapshot::getTicker).toList());
    }

    public List<Signal> getSignals() {
        return List.copyOf(signals);
    }
}
