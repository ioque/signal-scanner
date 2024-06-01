package ru.ioque.investfund.domain.scanner.algorithms.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlgorithmType {
    ANOMALY_VOLUME("Аномальные объемы"),
    PREF_COMMON("Дельта анализ цен пар преф-обычка"),
    SECTORAL_FUTURES("Корреляция сектора с фьючерсом на основной товар сектора"),
    SECTORAL_RETARD("Секторальный отстающий");
    private final String name;
}
