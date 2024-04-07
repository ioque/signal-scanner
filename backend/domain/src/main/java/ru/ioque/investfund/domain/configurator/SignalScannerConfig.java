package ru.ioque.investfund.domain.configurator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.core.DomainException;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SignalScannerConfig extends Domain {
    Integer workPeriodInMinutes;
    String description;
    List<String> tickers;
    AlgorithmConfig algorithmConfig;

    @Builder
    public SignalScannerConfig(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        List<String> tickers,
        AlgorithmConfig algorithmConfig
    ) {
        super(id);
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.tickers = tickers;
        this.algorithmConfig = algorithmConfig;
        validate();
    }

    private void validate() {
        if (workPeriodInMinutes == null) {
            throw new DomainException("Не передан период работы сканера.");
        }

        if (algorithmConfig == null) {
            throw new DomainException("Не передана конфигурация алгоритма.");
        }

        if (description == null || description.isBlank()) {
            throw new DomainException("Не передано описание.");
        }

        if (tickers == null || tickers.isEmpty()) {
            throw new DomainException("Не передан список анализируемых инструментов.");
        }
    }
}
