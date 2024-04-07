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
import ru.ioque.investfund.domain.configurator.CorrelationSectoralAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.SignalScannerConfig;
import ru.ioque.investfund.domain.scanner.entity.CorrelationSectoralAlgorithm;
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
        List<String> tickers,
        List<SignalEntity> signals,
        LocalDateTime lastWorkDateTime,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(id, workPeriodInMinutes, description, tickers, lastWorkDateTime, signals);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    public static ScannerEntity from(SignalScannerConfig config) {
        CorrelationSectoralAlgorithmConfig algorithmConfig = (CorrelationSectoralAlgorithmConfig) config.getAlgorithmConfig();
        return CorrelationSectoralScannerEntity.builder()
            .id(config.getId())
            .workPeriodInMinutes(config.getWorkPeriodInMinutes())
            .description(config.getDescription())
            .tickers(config.getTickers())
            .lastWorkDateTime(null)
            .signals(new ArrayList<>())
            .futuresOvernightScale(algorithmConfig.getFuturesOvernightScale())
            .stockOvernightScale(algorithmConfig.getStockOvernightScale())
            .futuresTicker(algorithmConfig.getFuturesTicker())
            .build();
    }

    public static ScannerEntity from(SignalScanner signalScanner) {
        CorrelationSectoralAlgorithm algorithm = (CorrelationSectoralAlgorithm) signalScanner.getAlgorithm();
        return CorrelationSectoralScannerEntity.builder()
            .id(signalScanner.getId())
            .workPeriodInMinutes(signalScanner.getWorkPeriodInMinutes())
            .description(signalScanner.getDescription())
            .tickers(signalScanner.getTickers())
            .lastWorkDateTime(signalScanner.getLastExecutionDateTime().orElse(null))
            .signals(signalScanner.getSignals().stream().map(SignalEntity::from).toList())
            .futuresOvernightScale(algorithm.getFuturesOvernightScale())
            .stockOvernightScale(algorithm.getStockOvernightScale())
            .futuresTicker(algorithm.getFuturesTicker())
            .build();
    }

    @Override
    public SignalScanner toDomain(List<TradingSnapshot> tradingSnapshots) {
        return SignalScanner.builder()
            .id(getId())
            .algorithm(
                CorrelationSectoralAlgorithmConfig
                    .builder()
                    .futuresOvernightScale(futuresOvernightScale)
                    .stockOvernightScale(stockOvernightScale)
                    .futuresTicker(futuresTicker)
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
        CorrelationSectoralAlgorithmConfig algorithmConfig = (CorrelationSectoralAlgorithmConfig) config.getAlgorithmConfig();
        this.futuresOvernightScale = algorithmConfig.getFuturesOvernightScale();
        this.stockOvernightScale = algorithmConfig.getStockOvernightScale();
        this.futuresTicker = algorithmConfig.getFuturesTicker();
    }
}
