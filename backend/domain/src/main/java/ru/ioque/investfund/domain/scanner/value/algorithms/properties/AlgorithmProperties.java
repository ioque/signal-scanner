package ru.ioque.investfund.domain.scanner.value.algorithms.properties;

import ru.ioque.investfund.domain.scanner.value.algorithms.AlgorithmType;

public interface AlgorithmProperties {
    AlgorithmType getType();
    String prettyPrint();
}
