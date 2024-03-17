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
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
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
    ScannerAlgorithm algorithm;
    @NonFinal
    LocalDateTime lastExecutionDateTime;
    List<Signal> signals;
    List<FinInstrument> finInstruments;

    @Builder
    public SignalScanner(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        ScannerAlgorithm algorithm,
        LocalDateTime lastExecutionDateTime,
        List<FinInstrument> finInstruments,
        List<Signal> signals
    ) {
        super(id);

        if (workPeriodInMinutes == null) {
            throw new DomainException("Не передан период работы сканера.");
        }

        if (algorithm == null) {
            throw new DomainException("Не передан алгоритм сканера.");
        }

        if (description == null || description.isBlank()) {
            throw new DomainException("Не передано описание.");
        }

        this.workPeriodInMinutes = workPeriodInMinutes;
        this.algorithm = algorithm;
        this.description = description;
        this.lastExecutionDateTime = lastExecutionDateTime;
        this.signals = signals != null ? new ArrayList<>(signals) : new ArrayList<>();
        this.finInstruments = finInstruments != null ? new ArrayList<>(finInstruments) : new ArrayList<>();
    }

    public Optional<LocalDateTime> getLastExecutionDateTime() {
        return Optional.ofNullable(lastExecutionDateTime);
    }

    public List<ScannerLog> scanning(LocalDateTime dateTimeNow) {
        if (finInstruments.isEmpty()) {
            throw new DomainException("Нет статистических данных для выбранных инструментов.");
        }
        return processResult(algorithm.run(getId(), finInstruments, dateTimeNow));
    }

    private List<ScannerLog> processResult(ScanningResult scanningResult) {
        List<Signal> oldSignals = List.copyOf(this.signals);
        List<Signal> newSignals = List.copyOf(scanningResult.getSignals());
        List<Signal> finalSignalList = new ArrayList<>(newSignals.stream().filter(Signal::isBuy).toList());
        newSignals
            .stream()
            .filter(newSignal -> !newSignal.isBuy())
            .forEach(newSignal -> {
                    if (oldSignals.stream().anyMatch(oldSignal -> newSignal.sameByInstrumentId(oldSignal)
                        && oldSignal.isBuy())) {
                        finalSignalList.add(newSignal);
                    }
                }
            );
        oldSignals.forEach(oldSignal -> {
            if (newSignals.stream().noneMatch(newSignal -> newSignal.sameByInstrumentId(oldSignal))) {
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

    public List<FinInstrument> getFinInstruments() {
        return List.copyOf(finInstruments);
    }

    public List<UUID> getObjectIds() {
        return List.copyOf(finInstruments.stream().map(FinInstrument::getId).toList());
    }

    public List<Signal> getSignals() {
        return List.copyOf(signals);
    }
}
