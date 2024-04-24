package ru.ioque.investfund.adapters.persistence.entity.scanner;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
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
public class AnomalyVolumeScannerEntity extends ScannerEntity {
    Double scaleCoefficient;
    Integer historyPeriod;
    String indexTicker;

    @Builder
    public AnomalyVolumeScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<UUID> instrumentIds,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(id, workPeriodInMinutes, description, datasourceId, instrumentIds, lastWorkDateTime, signals);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    public static ScannerEntity from(SignalScanner scannerDomain) {
        AnomalyVolumeProperties properties = (AnomalyVolumeProperties) scannerDomain.getProperties();
        AnomalyVolumeScannerEntity scannerEntity = AnomalyVolumeScannerEntity.builder()
            .id(scannerDomain.getId().getUuid())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId().getUuid())
            .instrumentIds(scannerDomain.getInstrumentIds().stream().map(InstrumentId::getUuid).toList())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .scaleCoefficient(properties.getScaleCoefficient())
            .historyPeriod(properties.getHistoryPeriod())
            .indexTicker(properties.getIndexTicker().getValue())
            .build();
        List<SignalEntity> signals = scannerDomain
            .getSignals()
            .stream()
            .map(signal -> SignalEntity.from(scannerEntity, signal))
            .toList();
        scannerEntity.setSignals(signals);
        return scannerEntity;
    }

    @Override
    @Transient
    public AlgorithmProperties getAlgorithmProperties() {
        return AnomalyVolumeProperties
            .builder()
            .scaleCoefficient(scaleCoefficient)
            .historyPeriod(historyPeriod)
            .indexTicker(new Ticker(indexTicker))
            .build();
    }
}
