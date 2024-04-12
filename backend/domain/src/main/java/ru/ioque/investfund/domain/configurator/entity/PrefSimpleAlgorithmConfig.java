package ru.ioque.investfund.domain.configurator.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.value.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.value.algorithms.PrefSimpleAlgorithm;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefSimpleAlgorithmConfig extends AlgorithmConfig {
    Double spreadParam;

    @Builder
    public PrefSimpleAlgorithmConfig(Double spreadParam) {
        setSpreadParam(spreadParam);
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new PrefSimpleAlgorithm(spreadParam);
    }

    private void setSpreadParam(Double spreadParam) {
        if (spreadParam == null) {
            throw new DomainException("Не передан параметр spreadParam.");
        }
        if (spreadParam <= 0) {
            throw new DomainException("Параметр spreadParam должен быть больше нуля.");
        }
        this.spreadParam = spreadParam;
    }
}
