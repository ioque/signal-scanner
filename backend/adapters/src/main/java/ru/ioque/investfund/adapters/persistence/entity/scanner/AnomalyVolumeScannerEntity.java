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
import ru.ioque.investfund.domain.configurator.AnomalyVolumeAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.SignalScannerConfig;
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
        List<String> tickers,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(id, workPeriodInMinutes, description, tickers, lastWorkDateTime, signals);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    public static ScannerEntity from(SignalScannerConfig config) {
        AnomalyVolumeAlgorithmConfig algorithmConfig = (AnomalyVolumeAlgorithmConfig) config.getAlgorithmConfig();
        return AnomalyVolumeScannerEntity.builder()
            .id(config.getId())
            .workPeriodInMinutes(config.getWorkPeriodInMinutes())
            .description(config.getDescription())
            .tickers(config.getTickers())
            .lastWorkDateTime(null)
            .signals(new ArrayList<>())
            .scaleCoefficient(algorithmConfig.getScaleCoefficient())
            .historyPeriod(algorithmConfig.getHistoryPeriod())
            .indexTicker(algorithmConfig.getIndexTicker())
            .build();
    }

    public static ScannerEntity from(SignalScanner signalScanner) {
        AnomalyVolumeAlgorithm algorithm = (AnomalyVolumeAlgorithm) signalScanner.getAlgorithm();
        return AnomalyVolumeScannerEntity.builder()
            .id(signalScanner.getId())
            .workPeriodInMinutes(signalScanner.getWorkPeriodInMinutes())
            .description(signalScanner.getDescription())
            .tickers(signalScanner.getTickers())
            .lastWorkDateTime(signalScanner.getLastExecutionDateTime().orElse(null))
            .signals(signalScanner.getSignals().stream().map(SignalEntity::from).toList())
            .scaleCoefficient(algorithm.getScaleCoefficient())
            .historyPeriod(algorithm.getHistoryPeriod())
            .indexTicker(algorithm.getIndexTicker())
            .build();
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
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .tradingSnapshots(tradingSnapshots)
            .lastExecutionDateTime(getLastExecutionDateTime())
            .signals(getSignals().stream().map(SignalEntity::toDomain).toList())
            .build();
    }

    @Override
    public void updateConfig(SignalScannerConfig config) {
        AnomalyVolumeAlgorithmConfig algorithmConfig = (AnomalyVolumeAlgorithmConfig) config.getAlgorithmConfig();
        this.scaleCoefficient = algorithmConfig.getScaleCoefficient();
        this.indexTicker = algorithmConfig.getIndexTicker();
        this.historyPeriod = algorithmConfig.getHistoryPeriod();
    }
}
