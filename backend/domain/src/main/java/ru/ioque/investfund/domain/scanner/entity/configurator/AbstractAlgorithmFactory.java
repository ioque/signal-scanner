package ru.ioque.investfund.domain.scanner.entity.configurator;

import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;

public class AbstractAlgorithmFactory {
    public ScannerAlgorithm factory(ScannerConfiguration configuration) {
        return configuration.factoryAlgorithm();
    }
}
