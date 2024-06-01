package ru.ioque.investfund.domain.scanner.algorithms.core;

import ru.ioque.investfund.domain.scanner.algorithms.impl.AnomalyVolumeAlgorithm;
import ru.ioque.investfund.domain.scanner.algorithms.impl.PrefCommonAlgorithm;
import ru.ioque.investfund.domain.scanner.algorithms.impl.SectoralFuturesAlgorithm;
import ru.ioque.investfund.domain.scanner.algorithms.impl.SectoralRetardAlgorithm;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;

import java.util.Map;
import java.util.function.Function;

public class AlgorithmFactory {
    public static ScannerAlgorithm factoryBy(AlgorithmProperties properties) {
        return factories.get(properties.getType()).apply(properties);
    }

    private static final Map<AlgorithmType, Function<AlgorithmProperties, ScannerAlgorithm>> factories = Map.of(
        AlgorithmType.ANOMALY_VOLUME, properties -> new AnomalyVolumeAlgorithm((AnomalyVolumeProperties) properties),
        AlgorithmType.SECTORAL_RETARD, properties -> new SectoralRetardAlgorithm((SectoralRetardProperties) properties),
        AlgorithmType.SECTORAL_FUTURES, properties -> new SectoralFuturesAlgorithm((SectoralFuturesProperties) properties),
        AlgorithmType.PREF_COMMON, properties -> new PrefCommonAlgorithm((PrefCommonProperties) properties)
    );
}
