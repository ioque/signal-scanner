package ru.ioque.investfund.domain.scanner.financial.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ioque.investfund.domain.Domain;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;

import java.time.LocalDateTime;
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
    //Грузить сюда сразу статистики, потому как этот лист нахер не нужен, данные агрегат работает со статистиками
    List<UUID> objectIds;
    @Getter
    SignalConfig config;
    @NonFinal
    LocalDateTime lastExecutionDateTime;

    @Builder
    public SignalScannerBot(
        UUID id,
        String description,
        List<UUID> objectIds,
        SignalConfig config,
        LocalDateTime lastExecutionDateTime
    ) {
        super(id);
        this.config = config;
        this.objectIds = objectIds;
        this.description = description;
        this.lastExecutionDateTime = lastExecutionDateTime;
    }

    public Optional<LocalDateTime> getLastExecutionDateTime() {
        return Optional.ofNullable(lastExecutionDateTime);
    }

    public Report scanning(List<InstrumentStatistic> statistics, LocalDateTime dateTimeNow) {
        if (statistics.isEmpty()) {
            throw new DomainException("Нет статистических данных для выбранных инструментов.");
        }
        lastExecutionDateTime = dateTimeNow;
        return config.factorySearchAlgorithm().run(getId(), statistics, dateTimeNow);
    }

    public boolean isTimeForExecution(LocalDateTime nowDateTime) {
        if (getLastExecutionDateTime().isEmpty()) return true;
        return config.isTimeForExecution(lastExecutionDateTime, nowDateTime);
    }
}
