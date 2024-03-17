package ru.ioque.investfund.domain.scanner.entity.algorithms.anomalyvolume;

import ru.ioque.investfund.domain.core.DomainException;

public class AnomalyVolumeAlgorithmValidator {
    AnomalyVolumeAlgorithmValidator(AnomalyVolumeAlgorithm algorithm) {
        if (algorithm.getScaleCoefficient() == null) {
            throw new DomainException("Не передан параметр scaleCoefficient.");
        }
        if (algorithm.getHistoryPeriod() == null) {
            throw new DomainException("Не передан параметр historyPeriod.");
        }
        if (algorithm.getIndexTicker() == null || algorithm.getIndexTicker().isEmpty()) {
            throw new DomainException("Не передан параметр indexTicker.");
        }
        if (algorithm.getScaleCoefficient() <= 0) {
            throw new DomainException("Параметр scaleCoefficient должен быть больше нуля.");
        }
        if (algorithm.getHistoryPeriod() <= 0) {
            throw new DomainException("Параметр historyPeriod должен быть больше нуля.");
        }
    }
}
