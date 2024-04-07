package ru.ioque.investfund.domain.configurator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.algorithms.prefsimplepair.PrefSimpleAlgorithm;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PrefSimpleAlgorithmConfig extends AlgorithmConfig {
    Double spreadParam;

    @Builder
    public PrefSimpleAlgorithmConfig(Double spreadParam) {
        this.spreadParam = spreadParam;
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new PrefSimpleAlgorithm(spreadParam);
    }
}
