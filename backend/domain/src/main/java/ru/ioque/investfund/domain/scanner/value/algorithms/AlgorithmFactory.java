package ru.ioque.investfund.domain.scanner.value.algorithms;

import ru.ioque.investfund.domain.scanner.value.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.SectoralFuturesProperties;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.SectoralRetardProperties;

import java.util.Map;
import java.util.function.Function;

public class AlgorithmFactory {
    public ScannerAlgorithm factoryBy(AlgorithmProperties properties) {
        return factories.get(properties.getType()).apply(properties);
    }

    Map<AlgorithmType, Function<AlgorithmProperties, ScannerAlgorithm>> factories = Map.of(
        AlgorithmType.ANOMALY_VOLUME, properties -> new AnomalyVolumeAlgorithm((AnomalyVolumeProperties) properties),
        AlgorithmType.SECTORAL_RETARD, properties -> new SectoralRetardAlgorithm((SectoralRetardProperties) properties),
        AlgorithmType.SECTORAL_FUTURES, properties -> new SectoralFuturesAlgorithm((SectoralFuturesProperties) properties),
        AlgorithmType.PREF_COMMON, properties -> new PrefCommonAlgorithm((PrefCommonProperties) properties)
    );
}
