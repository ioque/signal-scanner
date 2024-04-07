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
        List<String> tickers,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double historyScale,
        Double intradayScale
    ) {
        super(id, workPeriodInMinutes, description, tickers, lastWorkDateTime, signals);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    public static ScannerEntity from(SignalScannerConfig config) {
        SectoralRetardAlgorithmConfig algorithmConfig = (SectoralRetardAlgorithmConfig) config.getAlgorithmConfig();
        return SectoralRetardScannerEntity.builder()
            .id(config.getId())
            .workPeriodInMinutes(config.getWorkPeriodInMinutes())
            .description(config.getDescription())
            .tickers(config.getTickers())
            .lastWorkDateTime(null)
            .signals(new ArrayList<>())
            .historyScale(algorithmConfig.getHistoryScale())
            .intradayScale(algorithmConfig.getIntradayScale())
            .build();
    }

    public static ScannerEntity from(SignalScanner signalScanner) {
        SectoralRetardAlgorithm config = (SectoralRetardAlgorithm) signalScanner.getAlgorithm();
        return SectoralRetardScannerEntity.builder()
            .id(signalScanner.getId())
            .workPeriodInMinutes(signalScanner.getWorkPeriodInMinutes())
            .description(signalScanner.getDescription())
            .tickers(signalScanner.getTickers())
            .lastWorkDateTime(signalScanner.getLastExecutionDateTime().orElse(null))
            .signals(signalScanner.getSignals().stream().map(SignalEntity::from).toList())
            .historyScale(config.getHistoryScale())
            .intradayScale(config.getIntradayScale())
            .build();
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
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .tradingSnapshots(tradingSnapshots)
            .lastExecutionDateTime(getLastExecutionDateTime())
            .signals(getSignals().stream().map(SignalEntity::toDomain).toList())
            .build();
    }

    @Override
    public void updateConfig(SignalScannerConfig config) {
        SectoralRetardAlgorithmConfig algorithmConfig = (SectoralRetardAlgorithmConfig) config.getAlgorithmConfig();
        this.historyScale = algorithmConfig.getHistoryScale();
        this.intradayScale = algorithmConfig.getIntradayScale();
    }
}