package ru.ioque.investfund.domain.configurator.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.core.DomainException;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScannerConfig extends Domain {
    Integer workPeriodInMinutes;
    String description;
    UUID datasourceId;
    List<String> tickers;
    AlgorithmConfig algorithmConfig;

    @Builder
    public ScannerConfig(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        AlgorithmConfig algorithmConfig
    ) {
        super(id);
        setWorkPeriodInMinutes(workPeriodInMinutes);
        setDescription(description);
        setDatasourceId(datasourceId);
        setTickers(tickers);
        setAlgorithmConfig(algorithmConfig);
    }

    private void setWorkPeriodInMinutes(Integer workPeriodInMinutes) {
        if (workPeriodInMinutes == null) {
            throw new DomainException("Не передан период работы сканера.");
        }
        if (workPeriodInMinutes <= 0) {
            throw new DomainException("Период работы сканера должен быть положительным целым числом.");
        }
        this.workPeriodInMinutes = workPeriodInMinutes;
    }

    private void setDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new DomainException("Не передано описание.");
        }
        this.description = description;
    }

    private void setTickers(List<String> tickers) {
        if (tickers == null || tickers.isEmpty()) {
            throw new DomainException("Не передан список тикеров анализируемых инструментов.");
        }
        this.tickers = tickers;
    }

    private void setDatasourceId(UUID datasourceId) {
        this.datasourceId = datasourceId;
    }

    private void setAlgorithmConfig(AlgorithmConfig algorithmConfig) {
        if (algorithmConfig == null) {
            throw new DomainException("Не передана конфигурация алгоритма.");
        }
        this.algorithmConfig = algorithmConfig;
    }
}
