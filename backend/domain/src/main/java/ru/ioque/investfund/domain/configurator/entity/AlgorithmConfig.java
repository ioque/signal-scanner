package ru.ioque.investfund.domain.configurator.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.scanner.entity.ScannerAlgorithm;

@Getter
@ToString
@EqualsAndHashCode
public abstract class AlgorithmConfig {
    public abstract ScannerAlgorithm factoryAlgorithm();
}
