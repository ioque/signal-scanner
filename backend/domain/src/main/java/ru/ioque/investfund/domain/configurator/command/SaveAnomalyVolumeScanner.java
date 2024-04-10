package ru.ioque.investfund.domain.configurator.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.configurator.entity.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.AnomalyVolumeAlgorithmConfig;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SaveAnomalyVolumeScanner extends SaveScannerCommand {
    Double scaleCoefficient;
    Integer historyPeriod;
    String indexTicker;

    @Builder
    public SaveAnomalyVolumeScanner(
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(workPeriodInMinutes, description, datasourceId, tickers);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    @Override
    public AlgorithmConfig buildConfig() {
        return AnomalyVolumeAlgorithmConfig.builder()
            .historyPeriod(historyPeriod)
            .scaleCoefficient(scaleCoefficient)
            .indexTicker(indexTicker)
            .build();
    }
}
