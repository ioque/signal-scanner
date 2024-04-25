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
import ru.ioque.investfund.domain.scanner.value.ScanningResult;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalScanner extends Domain<ScannerId> {
    String description;
    final DatasourceId datasourceId;
    List<InstrumentId> instrumentIds;
    Integer workPeriodInMinutes;
    AlgorithmProperties properties;
    LocalDateTime lastExecutionDateTime;
    final List<Signal> signals;
    final Set<String> logs = new TreeSet<>();

    @Builder
    public SignalScanner(
        ScannerId id,
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

    public void updateWorkPeriod(Integer workPeriodInMinutes) {
        this.workPeriodInMinutes = workPeriodInMinutes;
    }

    public void updateInstrumentIds(List<InstrumentId> instrumentIds) {
        this.instrumentIds = instrumentIds;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateProperties(AlgorithmProperties properties) {
        if (!properties.getType().equals(getProperties().getType())) {
            throw new IllegalArgumentException("Невозможно изменить тип алгоритма.");
        }
        this.properties = properties;
    }

    public Optional<LocalDateTime> getLastExecutionDateTime() {
        return Optional.ofNullable(lastExecutionDateTime);
    }

    public List<Signal> scanning(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        if (tradingSnapshots.isEmpty()) {
            throw new DomainException("Нет статистических данных для выбранных инструментов.");
        }
        final AlgorithmFactory algorithmFactory = new AlgorithmFactory();
        final ScannerAlgorithm algorithm = algorithmFactory.factoryBy(properties);
        lastExecutionDateTime = watermark;
        logs.add(String.format(
            "Начал работу сканер[id=%s], алгоритм %s.",
            getId(),
            getProperties().getType().getName()
        ));
        final ScanningResult result = algorithm.run(tradingSnapshots, watermark);
        logs.addAll(result.getLogs());
        final List<Signal> newSignals = deduplicationNewSignals(result.getSignals());
        final List<Signal> registeredSignals = registerNewSignals(newSignals);
        logs.add(String.format(
            "Завершил работу сканер[id=%s], алгоритм %s. Найдено %s сигналов, зарегистрировано %s",
            getId(),
            getProperties().getType().getName(),
            newSignals.size(),
            registeredSignals.size()
        ));
        return registeredSignals;
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

    private List<Signal> registerNewSignals(List<Signal> newSignals) {
        return newSignals.stream().filter(this::registerNewSignal).toList();
    }

    private boolean registerNewSignal(Signal newSignal) {
        Optional<Signal> signalSameByTicker =
            signals.stream().filter(signal -> signal.sameByTicker(newSignal)).findFirst();
        if (signalSameByTicker.isPresent()) {
            if (signalSameByTicker.get().isBuy() && newSignal.isSell()) {
                signalSameByTicker.get().close();
                signals.add(newSignal);
                return true;
            }
            if (signalSameByTicker.get().isSell() && newSignal.isBuy()) {
                signalSameByTicker.get().close();
                signals.add(newSignal);
                return true;
            }
            return false;
        }
        if (newSignal.isBuy()) {
            signals.add(newSignal);
            return true;
        }
        return false;
    }
}
