package ru.ioque.investfund.domain.configurator.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.configurator.entity.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.SectoralRetardAlgorithmConfig;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SaveSectoralRetardScanner extends SaveScannerCommand {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SaveSectoralRetardScanner(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        Double historyScale,
        Double intradayScale
    ) {
        super(workPeriodInMinutes, description, datasourceId, tickers);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    @Override
    public AlgorithmConfig buildConfig() {
        return SectoralRetardAlgorithmConfig.builder()
            .historyScale(historyScale)
            .intradayScale(intradayScale)
            .build();
    }
}
