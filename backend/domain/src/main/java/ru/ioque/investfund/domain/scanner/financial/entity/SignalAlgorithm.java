package ru.ioque.investfund.domain.scanner.financial.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public abstract class SignalAlgorithm {
    protected String name;
    public SignalAlgorithm(String name) {
        this.name = name;
    }
    public abstract Report run(UUID scannerId, List<InstrumentStatistic> statistics, LocalDateTime dateTimeNow);
}
