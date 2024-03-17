package ru.ioque.investfund.domain.scanner.entity.algorithms.sectoralretard;

import ru.ioque.investfund.domain.core.DomainException;

public class SectoralRetardAlgorithmValidator {
    SectoralRetardAlgorithmValidator(SectoralRetardAlgorithm algorithm) {
        if (algorithm.getHistoryScale() == null) {
            throw new DomainException("Не передан параметр historyScale.");
        }
        if (algorithm.getHistoryScale() <= 0) {
            throw new DomainException("Параметр historyScale должен быть больше нуля.");
        }
        if (algorithm.getIntradayScale() == null) {
            throw new DomainException("Не передан параметр intradayScale.");
        }
        if (algorithm.getIntradayScale() <= 0) {
            throw new DomainException("Параметр intradayScale должен быть больше нуля.");
        }
    }
}
