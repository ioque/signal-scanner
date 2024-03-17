package ru.ioque.investfund.domain.scanner.entity.configurator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.entity.algorithms.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.algorithms.PrefSimpleAlgorithm;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PrefSimpleScannerConfiguration extends ScannerConfiguration {
    private final Double spreadParam;

    @Builder
    public PrefSimpleScannerConfiguration(
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        Double spreadParam) {
        super(workPeriodInMinutes, description, objectIds);
        this.spreadParam = spreadParam;
        validate();
    }

    private void validate() {
        if (spreadParam == null) {
            throw new DomainException("Не передан параметр spreadParam.");
        }
        if (spreadParam <= 0) {
            throw new DomainException("Параметр spreadParam должен быть больше нуля.");
        }
    }

    @Override
    protected ScannerAlgorithm factoryAlgorithm() {
        return new PrefSimpleAlgorithm(spreadParam);
    }
}
