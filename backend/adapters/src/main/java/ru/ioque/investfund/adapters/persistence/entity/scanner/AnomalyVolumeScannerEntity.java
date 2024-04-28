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
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;

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
