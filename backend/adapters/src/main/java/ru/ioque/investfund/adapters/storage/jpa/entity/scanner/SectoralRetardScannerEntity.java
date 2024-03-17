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
import ru.ioque.investfund.domain.scanner.entity.algorithms.SectoralRetardAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.configurator.SectoralRetardScannerConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("SectoralRetardScannerEntity")
public class SectoralRetardScannerEntity extends SignalScannerEntity {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SectoralRetardScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        List<UUID> objectIds,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double historyScale,
        Double intradayScale
    ) {
        super(id, workPeriodInMinutes, description, objectIds, lastWorkDateTime, signals);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    public static SignalScannerEntity from(SignalScanner signalScanner) {
        SectoralRetardAlgorithm config = (SectoralRetardAlgorithm) signalScanner.getAlgorithm();
        return SectoralRetardScannerEntity.builder()
            .id(signalScanner.getId())
            .workPeriodInMinutes(signalScanner.getWorkPeriodInMinutes())
            .description(signalScanner.getDescription())
            .objectIds(signalScanner.getObjectIds())
            .lastWorkDateTime(signalScanner.getLastExecutionDateTime().orElse(null))
            .signals(signalScanner.getSignals().stream().map(SignalEntity::from).toList())
            .historyScale(config.getHistoryScale())
            .intradayScale(config.getIntradayScale())
            .build();
    }

    @Override
    public SignalScanner toDomain(List<FinInstrument> instruments) {
        return SectoralRetardScannerConfiguration
            .builder()
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .objectIds(getObjectIds())
            .historyScale(historyScale)
            .intradayScale(intradayScale)
            .build()
            .factoryScanner(
                getId(),
                getLastWorkDateTime(),
                instruments,
                getSignals().stream().map(SignalEntity::toDomain).toList()
            );
    }
}
