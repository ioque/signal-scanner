package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.AlgorithmConfig;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AddScannerCommand {
    Integer workPeriodInMinutes;
    String description;
    List<String> tickers;
    AlgorithmConfig algorithmConfig;

    @Builder
    public AddScannerCommand(
        Integer workPeriodInMinutes,
        String description,
        List<String> tickers,
        AlgorithmConfig algorithmConfig
    ) {
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.tickers = tickers;
        this.algorithmConfig = algorithmConfig;
    }
}
