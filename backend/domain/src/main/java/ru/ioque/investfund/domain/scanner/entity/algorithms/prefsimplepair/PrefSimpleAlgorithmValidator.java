package ru.ioque.investfund.domain.scanner.entity.algorithms.prefsimplepair;

import ru.ioque.investfund.domain.core.DomainException;

public class PrefSimpleAlgorithmValidator {
    PrefSimpleAlgorithmValidator(PrefSimpleAlgorithm algorithm) {
        if (algorithm.getSpreadParam() == null) {
            throw new DomainException("Не передан параметр spreadParam.");
        }
        if (algorithm.getSpreadParam() <= 0) {
            throw new DomainException("Параметр spreadParam должен быть больше нуля.");
        }
    }
}
