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
import ru.ioque.investfund.domain.scanner.entity.anomalyvolume.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("AnomalyVolumeScannerEntity")
public class AnomalyVolumeScannerEntity extends SignalScannerEntity {
    Double scaleCoefficient;
    Integer historyPeriod;
    String indexTicker;

    @Builder
    public AnomalyVolumeScannerEntity(
        UUID id,
        String description,
        List<UUID> objectIds,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(id, description, objectIds, lastWorkDateTime, signals);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    public static SignalScannerEntity from(SignalScanner signalScanner) {
        AnomalyVolumeSignalConfig config = (AnomalyVolumeSignalConfig) signalScanner.getConfig();
        return AnomalyVolumeScannerEntity.builder()
            .id(signalScanner.getId())
            .description(signalScanner.getDescription())
            .objectIds(signalScanner.getObjectIds())
            .lastWorkDateTime(signalScanner.getLastExecutionDateTime().orElse(null))
            .signals(signalScanner.getSignals().stream().map(SignalEntity::from).toList())
            .scaleCoefficient(config.getScaleCoefficient())
            .historyPeriod(config.getHistoryPeriod())
            .indexTicker(config.getIndexTicker())
            .build();
    }

    @Override
    public SignalScanner toDomain(List<FinInstrument> instruments) {
        return new SignalScanner(
            getId(),
            getDescription(),
            new AnomalyVolumeSignalConfig(getObjectIds(), scaleCoefficient, historyPeriod, indexTicker),
            new AnomalyVolumeSignalConfig(getObjectIds(), scaleCoefficient, historyPeriod, indexTicker).factorySearchAlgorithm(),
            getLastWorkDateTime(),
            getSignals().stream().map(SignalEntity::toDomain).toList(),
            instruments
        );
    }
}
