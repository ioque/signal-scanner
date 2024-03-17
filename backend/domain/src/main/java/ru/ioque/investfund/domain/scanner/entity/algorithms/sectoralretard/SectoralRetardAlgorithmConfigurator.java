package ru.ioque.investfund.domain.scanner.entity.algorithms.sectoralretard;

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
public class SectoralRetardAlgorithmConfigurator extends AlgorithmConfigurator {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SectoralRetardAlgorithmConfigurator(Double historyScale, Double intradayScale) {
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    @Override
    public ScannerAlgorithm factoryAlgorithm() {
        return new SectoralRetardAlgorithm(historyScale, intradayScale);
    }
}
