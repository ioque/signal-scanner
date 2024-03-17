package ru.ioque.investfund.domain.scanner.entity.configurator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.algorithms.AnomalyVolumeAlgorithm;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AnomalyVolumeScannerConfiguration extends ScannerConfiguration {
    private final Double scaleCoefficient;
    private final Integer historyPeriod;
    private final String indexTicker;

    @Builder
    public AnomalyVolumeScannerConfiguration(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(workPeriodInMinutes, description, objectIds);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
        validate();
    }

    private void validate() {
        if (scaleCoefficient == null) {
            throw new DomainException("Не передан параметр scaleCoefficient.");
        }
        if (historyPeriod == null) {
            throw new DomainException("Не передан параметр historyPeriod.");
        }
        if (indexTicker == null || indexTicker.isEmpty()) {
            throw new DomainException("Не передан параметр indexTicker.");
        }
        if (scaleCoefficient <= 0) {
            throw new DomainException("Параметр scaleCoefficient должен быть больше нуля.");
        }
        if (historyPeriod <= 0) {
            throw new DomainException("Параметр historyPeriod должен быть больше нуля.");
        }
    }

    @Override
    protected ScannerAlgorithm factoryAlgorithm() {
        return new AnomalyVolumeAlgorithm(scaleCoefficient, historyPeriod, indexTicker);
    }
}
