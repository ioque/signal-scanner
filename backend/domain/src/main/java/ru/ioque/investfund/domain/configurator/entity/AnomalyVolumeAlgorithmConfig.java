package ru.ioque.investfund.domain.configurator.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.value.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.value.algorithms.AnomalyVolumeAlgorithm;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
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
        setScaleCoefficient(scaleCoefficient);
        setHistoryPeriod(historyPeriod);
        setIndexTicker(indexTicker);
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new AnomalyVolumeAlgorithm(scaleCoefficient, historyPeriod, indexTicker);
    }

    private void setScaleCoefficient(Double scaleCoefficient) {
        if (scaleCoefficient == null) {
            throw new DomainException("Не передан параметр scaleCoefficient.");
        }
        if (scaleCoefficient <= 0) {
            throw new DomainException("Параметр scaleCoefficient должен быть больше нуля.");
        }
        this.scaleCoefficient = scaleCoefficient;
    }

    private void setHistoryPeriod(Integer historyPeriod) {
        if (historyPeriod == null) {
            throw new DomainException("Не передан параметр historyPeriod.");
        }
        if (historyPeriod <= 0) {
            throw new DomainException("Параметр historyPeriod должен быть больше нуля.");
        }
        this.historyPeriod = historyPeriod;
    }

    private void setIndexTicker(String indexTicker) {
        if (indexTicker == null || indexTicker.isEmpty()) {
            throw new DomainException("Не передан параметр indexTicker.");
        }
        this.indexTicker = indexTicker;
    }
}
