package ru.ioque.investfund.adapters.storage.jpa.entity.scanner;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.entity.algorithms.PrefSimpleAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.configurator.PrefSimpleScannerConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("PrefSimpleScannerEntity")
public class PrefSimpleScannerEntity extends SignalScannerEntity {
    Double spreadParam;

    @Builder
    public PrefSimpleScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double spreadParam
    ) {
        super(id, workPeriodInMinutes, description, objectIds, lastWorkDateTime, signals);
        this.spreadParam = spreadParam;
    }

    public static SignalScannerEntity from(SignalScanner signalScanner) {
        PrefSimpleAlgorithm algorithm = (PrefSimpleAlgorithm) signalScanner.getAlgorithm();
        return PrefSimpleScannerEntity.builder()
            .id(signalScanner.getId())
            .workPeriodInMinutes(signalScanner.getWorkPeriodInMinutes())
            .description(signalScanner.getDescription())
            .objectIds(signalScanner.getObjectIds())
            .lastWorkDateTime(signalScanner.getLastExecutionDateTime().orElse(null))
            .signals(signalScanner.getSignals().stream().map(SignalEntity::from).toList())
            .spreadParam(algorithm.getSpreadParam())
            .build();
    }

    @Override
    public SignalScanner toDomain(List<FinInstrument> instruments) {
        return PrefSimpleScannerConfiguration
            .builder()
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .objectIds(getObjectIds())
            .spreadParam(spreadParam)
            .build()
            .factoryScanner(
                getId(),
                getLastWorkDateTime(),
                instruments,
                getSignals().stream().map(SignalEntity::toDomain).toList()
            );
    }
}
