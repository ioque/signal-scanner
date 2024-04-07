package ru.ioque.investfund.domain.configurator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.algorithms.anomalyvolume.AnomalyVolumeAlgorithm;

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
        validate();
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new AnomalyVolumeAlgorithm(scaleCoefficient, historyPeriod, indexTicker);
    }

    private void validate() {
        if (getScaleCoefficient() == null) {
            throw new DomainException("Не передан параметр scaleCoefficient.");
        }
        if (getHistoryPeriod() == null) {
            throw new DomainException("Не передан параметр historyPeriod.");
        }
        if (getIndexTicker() == null || getIndexTicker().isEmpty()) {
            throw new DomainException("Не передан параметр indexTicker.");
        }
        if (getScaleCoefficient() <= 0) {
            throw new DomainException("Параметр scaleCoefficient должен быть больше нуля.");
        }
        if (getHistoryPeriod() <= 0) {
            throw new DomainException("Параметр historyPeriod должен быть больше нуля.");
        }
    }
}
