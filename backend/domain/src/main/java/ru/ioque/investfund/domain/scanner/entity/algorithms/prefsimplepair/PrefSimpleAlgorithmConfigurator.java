package ru.ioque.investfund.domain.scanner.entity.algorithms.prefsimplepair;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
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
        validate();
    }

    private void validate() {
        if (this.spreadParam == null) {
            throw new DomainException("Не передан параметр spreadParam.");
        }
        if (this.spreadParam <= 0) {
            throw new DomainException("Параметр spreadParam должен быть больше нуля.");
        }
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new PrefSimpleAlgorithm(spreadParam);
    }
}
