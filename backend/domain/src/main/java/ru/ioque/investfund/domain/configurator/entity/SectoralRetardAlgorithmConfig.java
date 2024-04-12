package ru.ioque.investfund.domain.configurator.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.value.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.value.algorithms.SectoralRetardAlgorithm;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralRetardAlgorithmConfig extends AlgorithmConfig {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SectoralRetardAlgorithmConfig(Double historyScale, Double intradayScale) {
        setHistoryScale(historyScale);
        setIntradayScale(intradayScale);
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new SectoralRetardAlgorithm(historyScale, intradayScale);
    }

    private void setIntradayScale(Double intradayScale) {
        if (intradayScale == null) {
            throw new DomainException("Не передан параметр intradayScale.");
        }
        if (intradayScale <= 0) {
            throw new DomainException("Параметр intradayScale должен быть больше нуля.");
        }
        this.intradayScale = intradayScale;
    }

    private void setHistoryScale(Double historyScale) {
        if (historyScale == null) {
            throw new DomainException("Не передан параметр historyScale.");
        }
        if (historyScale <= 0) {
            throw new DomainException("Параметр historyScale должен быть больше нуля.");
        }
        this.historyScale = historyScale;
    }
}
