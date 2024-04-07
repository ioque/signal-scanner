package ru.ioque.investfund.domain.configurator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.SectoralRetardAlgorithm;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SectoralRetardAlgorithmConfig extends AlgorithmConfig {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SectoralRetardAlgorithmConfig(Double historyScale, Double intradayScale) {
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
        validate();
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new SectoralRetardAlgorithm(historyScale, intradayScale);
    }

    private void validate() {
        if (getHistoryScale() == null) {
            throw new DomainException("Не передан параметр historyScale.");
        }
        if (getHistoryScale() <= 0) {
            throw new DomainException("Параметр historyScale должен быть больше нуля.");
        }
        if (getIntradayScale() == null) {
            throw new DomainException("Не передан параметр intradayScale.");
        }
        if (getIntradayScale() <= 0) {
            throw new DomainException("Параметр intradayScale должен быть больше нуля.");
        }
    }
}
