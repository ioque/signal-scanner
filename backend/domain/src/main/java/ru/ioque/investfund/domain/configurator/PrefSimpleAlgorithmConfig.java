package ru.ioque.investfund.domain.configurator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
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
        validate();
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new PrefSimpleAlgorithm(spreadParam);
    }

    private void validate() {
        if (getSpreadParam() == null) {
            throw new DomainException("Не передан параметр spreadParam.");
        }
        if (getSpreadParam() <= 0) {
            throw new DomainException("Параметр spreadParam должен быть больше нуля.");
        }
    }
}
