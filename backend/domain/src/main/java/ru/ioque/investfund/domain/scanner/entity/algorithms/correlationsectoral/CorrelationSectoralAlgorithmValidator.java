package ru.ioque.investfund.domain.scanner.entity.algorithms.correlationsectoral;

import ru.ioque.investfund.domain.core.DomainException;

public class CorrelationSectoralAlgorithmValidator {
    CorrelationSectoralAlgorithmValidator(CorrelationSectoralAlgorithm algorithm) {
        if (algorithm.getFuturesOvernightScale() == null) {
            throw new DomainException("Не передан параметр futuresOvernightScale.");
        }
        if (algorithm.getStockOvernightScale() == null) {
            throw new DomainException("Не передан параметр stockOvernightScale.");
        }
        if (algorithm.getFuturesTicker() == null || algorithm.getFuturesTicker().isEmpty()) {
            throw new DomainException("Не передан параметр futuresTicker.");
        }
        if (algorithm.getFuturesOvernightScale() <= 0) {
            throw new DomainException("Параметр futuresOvernightScale должен быть больше нуля.");
        }
        if (algorithm.getStockOvernightScale() <= 0) {
            throw new DomainException("Параметр stockOvernightScale должен быть больше нуля.");
        }
    }
}
