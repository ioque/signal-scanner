package ru.ioque.investfund.domain.scanner.entity.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class AlgorithmConfig {
    public abstract ScannerAlgorithm factoryAlgorithm();
}
