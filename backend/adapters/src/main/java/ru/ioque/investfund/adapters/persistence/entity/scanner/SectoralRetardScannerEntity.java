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
import ru.ioque.investfund.domain.configurator.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.SectoralRetardAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.SignalScannerConfig;
import ru.ioque.investfund.domain.scanner.entity.SectoralRetardAlgorithm;
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
@DiscriminatorValue("SectoralRetardScannerEntity")
public class SectoralRetardScannerEntity extends ScannerEntity {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SectoralRetardScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double historyScale,
        Double intradayScale
    ) {
        super(id, workPeriodInMinutes, description, datasourceId, tickers, lastWorkDateTime, signals);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    public static ScannerEntity from(SignalScannerConfig config) {
        SectoralRetardAlgorithmConfig algorithmConfig = (SectoralRetardAlgorithmConfig) config.getAlgorithmConfig();
        return SectoralRetardScannerEntity.builder()
            .id(config.getId())
            .workPeriodInMinutes(config.getWorkPeriodInMinutes())
            .description(config.getDescription())
            .datasourceId(config.getDatasourceId())
            .tickers(config.getTickers())
            .lastWorkDateTime(null)
            .signals(new ArrayList<>())
            .historyScale(algorithmConfig.getHistoryScale())
            .intradayScale(algorithmConfig.getIntradayScale())
            .build();
    }

    public static ScannerEntity from(SignalScanner scannerDomain) {
        SectoralRetardAlgorithm config = (SectoralRetardAlgorithm) scannerDomain.getAlgorithm();
        SectoralRetardScannerEntity scannerEntity = SectoralRetardScannerEntity.builder()
            .id(scannerDomain.getId())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId())
            .tickers(scannerDomain.getTickers())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .historyScale(config.getHistoryScale())
            .intradayScale(config.getIntradayScale())
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
                SectoralRetardAlgorithmConfig
                    .builder()
                    .historyScale(historyScale)
                    .intradayScale(intradayScale)
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
        SectoralRetardAlgorithmConfig algorithmConfig = (SectoralRetardAlgorithmConfig) config;
        this.historyScale = algorithmConfig.getHistoryScale();
        this.intradayScale = algorithmConfig.getIntradayScale();
    }
}