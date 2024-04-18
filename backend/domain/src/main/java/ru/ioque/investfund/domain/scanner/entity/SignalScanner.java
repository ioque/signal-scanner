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
import ru.ioque.investfund.domain.scanner.value.SignalSign;
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
    String description;
    UUID datasourceId;
    List<String> tickers;
    Integer workPeriodInMinutes;
    AlgorithmProperties properties;
    LocalDateTime lastExecutionDateTime;
    final List<Signal> signals;

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

    public static SignalScanner of(UUID id, CreateScannerCommand command) {
        return SignalScanner.builder()
            .id(id)
            .workPeriodInMinutes(command.getWorkPeriodInMinutes())
            .description(command.getDescription())
            .datasourceId(command.getDatasourceId())
            .tickers(command.getTickers())
            .properties(command.getProperties())
            .signals(new ArrayList<>())
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

    public List<SignalSign> scanning(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        if (tradingSnapshots.isEmpty()) {
            throw new DomainException("Нет статистических данных для выбранных инструментов.");
        }
        AlgorithmFactory algorithmFactory = new AlgorithmFactory();
        ScannerAlgorithm algorithm = algorithmFactory.factoryBy(properties);
        lastExecutionDateTime = watermark;
        return algorithm
            .run(tradingSnapshots, watermark)
            .stream()
            .filter(this::signalNotExists)
            .toList();
    }

    public void addNewSignals(List<Signal> newSignals) {
        List<Signal> oldSignals = List.copyOf(this.signals);
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
    }

    public boolean isTimeForExecution(LocalDateTime nowDateTime) {
        if (getLastExecutionDateTime().isEmpty()) return true;
        return isTimeForExecution(getLastExecutionDateTime().get(), nowDateTime);
    }

    private boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime) {
        return Duration.between(lastExecution, nowDateTime).toMinutes() >= workPeriodInMinutes;
    }

    private boolean signalNotExists(SignalSign newSignal) {
        return signals
            .stream()
            .noneMatch(signal ->
                signal.getTicker().equals(newSignal.getTicker()) &&
                    signal.isBuy() == newSignal.isBuy()
            );
    }
}
