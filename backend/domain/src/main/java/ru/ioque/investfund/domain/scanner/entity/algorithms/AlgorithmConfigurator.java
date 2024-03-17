package ru.ioque.investfund.domain.scanner.entity.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;

@Getter
@ToString
@EqualsAndHashCode
public abstract class AlgorithmConfigurator {
    public abstract ScannerAlgorithm factoryAlgorithm();
}
