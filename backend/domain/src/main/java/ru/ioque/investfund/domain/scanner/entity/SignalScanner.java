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
import ru.ioque.investfund.domain.scanner.value.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
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
        setWorkPeriodInMinutes(workPeriodInMinutes);
        setDescription(description);
        setDatasourceId(datasourceId);
        setAlgorithm(algorithm);
        setLastExecutionDateTime(lastExecutionDateTime);
        setTradingSnapshots(tradingSnapshots);
        setSignals(signals);
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

    private void setWorkPeriodInMinutes(Integer workPeriodInMinutes) {
        if(workPeriodInMinutes == null) {
            throw new DomainException("Не передан период работы сканера.");
        }
        if(workPeriodInMinutes <= 0) {
            throw new DomainException("Период работы сканера должен быть целым положительным числом.");
        }
        this.workPeriodInMinutes = workPeriodInMinutes;
    }

    private void setDescription(String description) {
        if(description == null || description.isEmpty()) {
            throw new DomainException("Не передано описание сканера.");
        }
        this.description = description;
    }

    private void setDatasourceId(UUID datasourceId) {
        if(datasourceId == null) {
            throw new DomainException("Не передан идентификатор источника данных.");
        }
        this.datasourceId = datasourceId;
    }

    private void setAlgorithm(ScannerAlgorithm algorithm) {
        if(algorithm == null) {
            throw new DomainException("Не передан алгоритм поиска сигналов.");
        }
        this.algorithm = algorithm;
    }

    private void setLastExecutionDateTime(LocalDateTime lastExecutionDateTime) {
        this.lastExecutionDateTime = lastExecutionDateTime;
    }

    private void setSignals(List<Signal> signals) {
        this.signals = signals != null ? new ArrayList<>(signals) : new ArrayList<>();
    }

    private void setTradingSnapshots(List<TradingSnapshot> tradingSnapshots) {
        if (tradingSnapshots == null || tradingSnapshots.isEmpty()) {
            throw new DomainException("Не передан список снэпшотов торговых данных.");
        }
        this.tradingSnapshots = tradingSnapshots;
    }
}
