package ru.ioque.investfund.domain.scanner.entity.configurator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.algorithms.SectoralRetardAlgorithm;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SectoralRetardScannerConfiguration extends ScannerConfiguration {
    private final Double historyScale;
    private final Double intradayScale;

    @Builder
    public SectoralRetardScannerConfiguration(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        Double historyScale,
        Double intradayScale) {
        super(workPeriodInMinutes, description, objectIds);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
        validate();
    }

    private void validate() {
        if (historyScale == null) {
            throw new DomainException("Не передан параметр historyScale.");
        }
        if (historyScale <= 0) {
            throw new DomainException("Параметр historyScale должен быть больше нуля.");
        }
        if (intradayScale == null) {
            throw new DomainException("Не передан параметр intradayScale.");
        }
        if (intradayScale <= 0) {
            throw new DomainException("Параметр intradayScale должен быть больше нуля.");
        }
    }

    @Override
    protected ScannerAlgorithm factoryAlgorithm() {
        return new SectoralRetardAlgorithm(historyScale, intradayScale);
    }
}
