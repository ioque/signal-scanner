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
import ru.ioque.investfund.domain.configurator.entity.SectoralCorrelationAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.ScannerConfig;
import ru.ioque.investfund.domain.scanner.value.algorithms.CorrelationSectoralAlgorithm;
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
@DiscriminatorValue("CorrelationSectoralScannerEntity")
public class CorrelationSectoralScannerEntity extends ScannerEntity {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;

    @Builder
    public CorrelationSectoralScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        List<SignalEntity> signals,
        LocalDateTime lastWorkDateTime,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(id, workPeriodInMinutes, description, datasourceId, tickers, lastWorkDateTime, signals);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    public static ScannerEntity from(ScannerConfig config) {
        SectoralCorrelationAlgorithmConfig algorithmConfig = (SectoralCorrelationAlgorithmConfig) config.getAlgorithmConfig();
        return CorrelationSectoralScannerEntity.builder()
            .id(config.getId())
            .workPeriodInMinutes(config.getWorkPeriodInMinutes())
            .description(config.getDescription())
            .datasourceId(config.getDatasourceId())
            .tickers(config.getTickers())
            .lastWorkDateTime(null)
            .signals(new ArrayList<>())
            .futuresOvernightScale(algorithmConfig.getFuturesOvernightScale())
            .stockOvernightScale(algorithmConfig.getStockOvernightScale())
            .futuresTicker(algorithmConfig.getFuturesTicker())
            .build();
    }

    public static ScannerEntity from(SignalScanner scannerDomain) {
        CorrelationSectoralAlgorithm algorithm = (CorrelationSectoralAlgorithm) scannerDomain.getAlgorithm();
        CorrelationSectoralScannerEntity scannerEntity = CorrelationSectoralScannerEntity.builder()
            .id(scannerDomain.getId())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId())
            .tickers(scannerDomain.getTickers())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .futuresOvernightScale(algorithm.getFuturesOvernightScale())
            .stockOvernightScale(algorithm.getStockOvernightScale())
            .futuresTicker(algorithm.getFuturesTicker())
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
                SectoralCorrelationAlgorithmConfig
                    .builder()
                    .futuresOvernightScale(futuresOvernightScale)
                    .stockOvernightScale(stockOvernightScale)
                    .futuresTicker(futuresTicker)
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
        SectoralCorrelationAlgorithmConfig algorithmConfig = (SectoralCorrelationAlgorithmConfig) config;
        this.futuresOvernightScale = algorithmConfig.getFuturesOvernightScale();
        this.stockOvernightScale = algorithmConfig.getStockOvernightScale();
        this.futuresTicker = algorithmConfig.getFuturesTicker();
    }
}
