package ru.ioque.investfund.domain.scanner.entity.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class AlgorithmConfigurator {
    public abstract ScannerAlgorithm factoryAlgorithm();
}
