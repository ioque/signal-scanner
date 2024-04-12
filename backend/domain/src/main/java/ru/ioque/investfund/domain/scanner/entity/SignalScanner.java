package ru.ioque.investfund.domain.scanner.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.algorithms.AlgorithmFactory;
import ru.ioque.investfund.domain.scanner.value.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.AlgorithmProperties;

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
    LocalDateTime lastExecutionDateTime;
    List<String> tickers;
    final List<Signal> signals;
    AlgorithmProperties properties;

    @Builder
    public SignalScanner(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        AlgorithmProperties properties,
        LocalDateTime lastExecutionDateTime,
        List<String> tickers,
        List<Signal> signals
    ) {
        super(id);
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.datasourceId = datasourceId;
        this.properties = properties;
        this.lastExecutionDateTime = lastExecutionDateTime;
        this.tickers = tickers;
        this.signals = signals;
    }

    public static SignalScanner from(UUID id, CreateScannerCommand command) {
        return SignalScanner.builder()
            .id(id)
            .workPeriodInMinutes(command.getWorkPeriodInMinutes())
            .description(command.getDescription())
            .datasourceId(command.getDatasourceId())
            .tickers(command.getTickers())
            .signals(new ArrayList<>())
            .properties(command.getProperties())
            .lastExecutionDateTime(null)
            .build();
    }

    public void update(UpdateScannerCommand command) {
        if (!command.getProperties().getType().equals(getProperties().getType())) {
            throw new IllegalArgumentException("Невозможно изменить тип алгоритма.");
        }
        this.workPeriodInMinutes = command.getWorkPeriodInMinutes();
        this.description = command.getDescription();
        this.datasourceId = command.getScannerId();
        this.tickers = command.getTickers();
        this.properties = command.getProperties();
    }

    public Optional<LocalDateTime> getLastExecutionDateTime() {
        return Optional.ofNullable(lastExecutionDateTime);
    }

    public List<ScannerLog> scanning(List<TradingSnapshot> tradingSnapshots, LocalDateTime dateTimeNow) {
        if (tradingSnapshots.isEmpty()) {
            throw new DomainException("Нет статистических данных для выбранных инструментов.");
        }
        AlgorithmFactory algorithmFactory = new AlgorithmFactory();
        ScannerAlgorithm algorithm = algorithmFactory.factoryBy(properties);
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

    public List<Signal> getSignals() {
        return List.copyOf(signals);
    }
}
