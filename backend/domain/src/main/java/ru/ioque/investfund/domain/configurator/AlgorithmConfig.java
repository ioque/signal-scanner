package ru.ioque.investfund.domain.configurator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;

@Getter
@ToString
@EqualsAndHashCode
public abstract class AlgorithmConfig {
    public abstract ScannerAlgorithm factoryAlgorithm();
}
