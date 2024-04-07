package ru.ioque.investfund.domain.scanner.entity.algorithms.anomalyvolume;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.AlgorithmConfig;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AnomalyVolumeAlgorithmConfig extends AlgorithmConfig {
    Double scaleCoefficient;
    Integer historyPeriod;
    String indexTicker;

    @Builder
    public AnomalyVolumeAlgorithmConfig(
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new AnomalyVolumeAlgorithm(scaleCoefficient, historyPeriod, indexTicker);
    }
}
