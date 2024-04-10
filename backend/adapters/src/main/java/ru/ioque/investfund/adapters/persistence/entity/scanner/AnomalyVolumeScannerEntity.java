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
import ru.ioque.investfund.domain.configurator.entity.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.AnomalyVolumeAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.ScannerConfig;
import ru.ioque.investfund.domain.scanner.entity.AnomalyVolumeAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public static ScannerEntity from(ScannerConfig config) {
        AnomalyVolumeAlgorithmConfig algorithmConfig = (AnomalyVolumeAlgorithmConfig) config.getAlgorithmConfig();
        return AnomalyVolumeScannerEntity.builder()
            .id(config.getId())
            .workPeriodInMinutes(config.getWorkPeriodInMinutes())
            .description(config.getDescription())
            .datasourceId(config.getDatasourceId())
            .tickers(config.getTickers())
            .lastWorkDateTime(null)
            .signals(new ArrayList<>())
            .scaleCoefficient(algorithmConfig.getScaleCoefficient())
            .historyPeriod(algorithmConfig.getHistoryPeriod())
            .indexTicker(algorithmConfig.getIndexTicker())
            .build();
    }

    public static ScannerEntity from(SignalScanner scannerDomain) {
        AnomalyVolumeAlgorithm algorithm = (AnomalyVolumeAlgorithm) scannerDomain.getAlgorithm();
        AnomalyVolumeScannerEntity scannerEntity = AnomalyVolumeScannerEntity.builder()
            .id(scannerDomain.getId())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId())
            .tickers(scannerDomain.getTickers())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .scaleCoefficient(algorithm.getScaleCoefficient())
            .historyPeriod(algorithm.getHistoryPeriod())
            .indexTicker(algorithm.getIndexTicker())
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
    public SignalScanner toDomain(List<TradingSnapshot> tradingSnapshots) {
        return SignalScanner.builder()
            .id(getId())
            .algorithm(
                AnomalyVolumeAlgorithmConfig
                    .builder()
                    .scaleCoefficient(scaleCoefficient)
                    .historyPeriod(historyPeriod)
                    .indexTicker(indexTicker)
                    .build()
                    .factoryAlgorithm()
            )
            .datasourceId(getDatasourceId())
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .tradingSnapshots(tradingSnapshots)
            .lastExecutionDateTime(getLastExecutionDateTime())
            .signals(getSignals().stream().map(SignalEntity::toDomain).toList())
            .build();
    }

    @Override
    public void updateAlgorithmConfig(AlgorithmConfig config) {
        AnomalyVolumeAlgorithmConfig algorithmConfig = (AnomalyVolumeAlgorithmConfig) config;
        this.scaleCoefficient = algorithmConfig.getScaleCoefficient();
        this.indexTicker = algorithmConfig.getIndexTicker();
        this.historyPeriod = algorithmConfig.getHistoryPeriod();
    }
}
