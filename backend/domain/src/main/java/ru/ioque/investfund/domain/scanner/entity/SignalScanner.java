package ru.ioque.investfund.domain.scanner.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.algorithms.AlgorithmFactory;
import ru.ioque.investfund.domain.scanner.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

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
    DatasourceId datasourceId;
    List<InstrumentId> instrumentIds;
    Integer workPeriodInMinutes;
    AlgorithmProperties properties;
    LocalDateTime lastExecutionDateTime;
    final List<Signal> signals;

    @Builder
    public SignalScanner(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        DatasourceId datasourceId,
        AlgorithmProperties properties,
        LocalDateTime lastExecutionDateTime,
        List<InstrumentId> instrumentIds,
        List<Signal> signals
    ) {
        super(id);
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.datasourceId = datasourceId;
        this.properties = properties;
        this.lastExecutionDateTime = lastExecutionDateTime;
        this.instrumentIds = instrumentIds;
        this.signals = signals;
    }

    public static SignalScanner of(UUID id, CreateScannerCommand command) {
        return SignalScanner.builder()
            .id(id)
            .workPeriodInMinutes(command.getWorkPeriodInMinutes())
            .description(command.getDescription())
            .datasourceId(command.getDatasourceId())
            .instrumentIds(command.getInstrumentIds())
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
        this.instrumentIds = command.getInstrumentIds();
        this.properties = command.getProperties();
    }

    public Optional<LocalDateTime> getLastExecutionDateTime() {
        return Optional.ofNullable(lastExecutionDateTime);
    }

    public List<Signal> scanning(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        if (tradingSnapshots.isEmpty()) {
            throw new DomainException("Нет статистических данных для выбранных инструментов.");
        }
        AlgorithmFactory algorithmFactory = new AlgorithmFactory();
        ScannerAlgorithm algorithm = algorithmFactory.factoryBy(properties);
        lastExecutionDateTime = watermark;
        List<Signal> newSignals = deduplicationNewSignals(algorithm.run(tradingSnapshots, watermark));
        registerNewSignals(newSignals);
        return newSignals;
    }

    public boolean isTimeForExecution(LocalDateTime nowDateTime) {
        if (getLastExecutionDateTime().isEmpty()) return true;
        return isTimeForExecution(getLastExecutionDateTime().get(), nowDateTime);
    }

    private boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime) {
        return Duration.between(lastExecution, nowDateTime).toMinutes() >= workPeriodInMinutes;
    }

    private List<Signal> deduplicationNewSignals(List<Signal> signals) {
        return signals.stream().filter(signal -> !containsSignal(signal)).toList();
    }

    private boolean containsSignal(Signal newSignal) {
        return signals.stream().anyMatch(newSignal::sameByBusinessKey);
    }

    private void registerNewSignals(List<Signal> newSignals) {
        newSignals.forEach(this::registerNewSignal);
    }

    private void registerNewSignal(Signal newSignal) {
        Optional<Signal> signalSameByTicker = signals.stream().filter(signal -> signal.sameByInstrumentId(newSignal)).findFirst();
        if (signalSameByTicker.isPresent() && newSignal.isSell()) {
            signals.add(newSignal);
        }
        if (newSignal.isBuy()) {
            signals.add(newSignal);
        }
        signalSameByTicker.ifPresent(Signal::close);
    }
}
