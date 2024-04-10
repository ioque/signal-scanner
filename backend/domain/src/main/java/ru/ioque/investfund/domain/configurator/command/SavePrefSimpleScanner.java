package ru.ioque.investfund.domain.configurator.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.configurator.entity.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.PrefSimpleAlgorithmConfig;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SavePrefSimpleScanner extends SaveScannerCommand {
    Double spreadParam;

    @Builder
    public SavePrefSimpleScanner(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        Double spreadParam
    ) {
        super(workPeriodInMinutes, description, datasourceId, tickers);
        this.spreadParam = spreadParam;
    }

    @Override
    public AlgorithmConfig buildConfig() {
        return PrefSimpleAlgorithmConfig.builder().spreadParam(spreadParam).build();
    }
}
