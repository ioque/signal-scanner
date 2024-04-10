package ru.ioque.investfund.domain.configurator.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.configurator.entity.AlgorithmConfig;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AddNewScannerCommand {
    Integer workPeriodInMinutes;
    String description;
    UUID datasourceId;
    List<String> tickers;
    AlgorithmConfig algorithmConfig;

    @Builder
    public AddNewScannerCommand(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        AlgorithmConfig algorithmConfig
    ) {
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.datasourceId = datasourceId;
        this.tickers = tickers;
        this.algorithmConfig = algorithmConfig;
    }
}
