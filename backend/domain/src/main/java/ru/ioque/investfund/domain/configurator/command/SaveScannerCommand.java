package ru.ioque.investfund.domain.configurator.command;

import lombok.AccessLevel;
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
public abstract class SaveScannerCommand {
    Integer workPeriodInMinutes;
    String description;
    UUID datasourceId;
    List<String> tickers;
    public abstract AlgorithmConfig buildConfig();

    public SaveScannerCommand(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers
    ) {
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.datasourceId = datasourceId;
        this.tickers = tickers;
    }
}
