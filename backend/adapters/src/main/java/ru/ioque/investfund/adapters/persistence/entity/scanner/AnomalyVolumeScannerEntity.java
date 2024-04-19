package ru.ioque.investfund.adapters.persistence.entity.scanner;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        List<String> tickers,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(id, workPeriodInMinutes, description, datasourceId, tickers, lastWorkDateTime, signals);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    public static ScannerEntity from(SignalScanner scannerDomain) {
        AnomalyVolumeProperties properties = (AnomalyVolumeProperties) scannerDomain.getProperties();
        AnomalyVolumeScannerEntity scannerEntity = AnomalyVolumeScannerEntity.builder()
            .id(scannerDomain.getId())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId())
            .tickers(scannerDomain.getInstrumentIds())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .scaleCoefficient(properties.getScaleCoefficient())
            .historyPeriod(properties.getHistoryPeriod())
            .indexTicker(properties.getIndexTicker())
            .build();
        List<SignalEntity> signals = scannerDomain
                .getSignals()
                .stream()
                .map(SignalEntity::from)
                .peek(row -> row.setScanner(scannerEntity))
                .toList();
        scannerEntity.setSignals(signals);
        return scannerEntity;
    }

    @Override
    public SignalScanner toDomain() {
        return SignalScanner.builder()
            .id(getId())
            .properties(
                AnomalyVolumeProperties
                    .builder()
                    .scaleCoefficient(scaleCoefficient)
                    .historyPeriod(historyPeriod)
                    .indexTicker(indexTicker)
                    .build()
            )
            .datasourceId(getDatasourceId())
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .tickers(getTickers())
            .lastExecutionDateTime(getLastExecutionDateTime())
            .signals(getSignals().stream().map(SignalEntity::toDomain).collect(Collectors.toCollection(ArrayList::new)))
            .build();
    }
}
