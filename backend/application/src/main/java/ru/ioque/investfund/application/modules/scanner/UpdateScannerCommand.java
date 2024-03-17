package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.algorithms.AlgorithmConfigurator;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateScannerCommand {
    UUID id;
    Integer workPeriodInMinutes;
    String description;
    List<UUID> objectIds;
    AlgorithmConfigurator algorithmConfigurator;

    @Builder
    public UpdateScannerCommand(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        AlgorithmConfigurator algorithmConfigurator
    ) {
        this.id = id;
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.objectIds = objectIds;
        this.algorithmConfigurator = algorithmConfigurator;
    }
}
