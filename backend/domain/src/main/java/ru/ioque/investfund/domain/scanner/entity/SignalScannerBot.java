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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalScannerBot extends Domain {
    @Getter
    String description;
    @Getter
    SignalConfig config;
    @NonFinal
    LocalDateTime lastExecutionDateTime;
    List<Signal> signals;
    List<FinInstrument> finInstruments;

    @Builder
    public SignalScannerBot(
        UUID id,
        String description,
        SignalConfig config,
        LocalDateTime lastExecutionDateTime,
        List<Signal> signals,
        List<FinInstrument> finInstruments
    ) {
        super(id);
        this.config = config;
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
        return processResult(config.factorySearchAlgorithm().run(getId(), finInstruments, dateTimeNow));
    }

    private List<ScannerLog> processResult(ScanningResult scanningResult) {
        List<Signal> oldSignals = List.copyOf(this.signals);
        List<Signal> newSignals = List.copyOf(scanningResult.getSignals());
        List<Signal> finalSignalList = new ArrayList<>(newSignals);
        oldSignals.forEach(oldSignal -> {
            boolean alreadyExists = newSignals.stream().anyMatch(row -> row.sameByInstrumentId(oldSignal) && row.sameByIsBuy(oldSignal));
            boolean isSellAfterBuy = newSignals.stream().anyMatch(row -> row.sameByInstrumentId(oldSignal) && oldSignal.isBuy() && !row.isBuy());
            if (!alreadyExists && !isSellAfterBuy) {
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
        return config.isTimeForExecution(lastExecutionDateTime, nowDateTime);
    }

    public List<FinInstrument> getFinInstruments() {
        return List.copyOf(finInstruments);
    }

    public List<UUID> getObjectIds() {
        return List.copyOf(config.getObjectIds());
    }

    public List<Signal> getSignals() {
        return List.copyOf(signals);
    }
}
