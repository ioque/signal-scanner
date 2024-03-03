package ru.ioque.investfund.domain.scanner.financial.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.Domain;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalScannerBot extends Domain {
    String description;
    //Грузить сюда сразу статистики, потому как этот лист нахер не нужен, данные агрегат работает со статистиками
    List<UUID> objectIds;
    SignalConfig config;

    public SignalScannerBot(
        UUID id,
        String description,
        List<UUID> objectIds,
        SignalConfig config
    ) {
        super(id);
        this.config = config;
        this.objectIds = objectIds;
        this.description = description;
    }

    public Report scanning(List<InstrumentStatistic> statistics, LocalDateTime dateTimeNow) {
        if (statistics.isEmpty()) {
            throw new DomainException("Нет статистических данных для выбранных инструментов.");
        }
        return config.factorySearchAlgorithm().run(getId(), statistics, dateTimeNow);
    }

    public boolean isTimeForExecution(LocalDateTime lastExecution, LocalDateTime nowDateTime) {
        return config.isTimeForExecution(lastExecution, nowDateTime);
    }
}
