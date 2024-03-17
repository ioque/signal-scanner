package ru.ioque.investfund.domain.scanner.entity.algorithms.prefsimplepair;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.AlgorithmConfigurator;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PrefSimpleAlgorithmConfigurator extends AlgorithmConfigurator {
    Double spreadParam;

    @Builder
    public PrefSimpleAlgorithmConfigurator(Double spreadParam) {
        this.spreadParam = spreadParam;
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new PrefSimpleAlgorithm(spreadParam);
    }
}
