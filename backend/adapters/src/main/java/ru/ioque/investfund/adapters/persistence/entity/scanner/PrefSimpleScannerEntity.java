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
import ru.ioque.investfund.domain.configurator.entity.PrefSimpleAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.ScannerConfig;
import ru.ioque.investfund.domain.scanner.entity.PrefSimpleAlgorithm;
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
@DiscriminatorValue("PrefSimpleScannerEntity")
public class PrefSimpleScannerEntity extends ScannerEntity {
    Double spreadParam;

    @Builder
    public PrefSimpleScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double spreadParam
    ) {
        super(id, workPeriodInMinutes, description, datasourceId, tickers, lastWorkDateTime, signals);
        this.spreadParam = spreadParam;
    }

    public static ScannerEntity from(ScannerConfig config) {
        PrefSimpleAlgorithmConfig algorithmConfig = (PrefSimpleAlgorithmConfig) config.getAlgorithmConfig();
        return PrefSimpleScannerEntity.builder()
            .id(config.getId())
            .workPeriodInMinutes(config.getWorkPeriodInMinutes())
            .description(config.getDescription())
            .datasourceId(config.getDatasourceId())
            .tickers(config.getTickers())
            .lastWorkDateTime(null)
            .signals(new ArrayList<>())
            .spreadParam(algorithmConfig.getSpreadParam())
            .build();
    }

    public static ScannerEntity from(SignalScanner scannerDomain) {
        PrefSimpleAlgorithm algorithm = (PrefSimpleAlgorithm) scannerDomain.getAlgorithm();
        PrefSimpleScannerEntity scannerEntity = PrefSimpleScannerEntity.builder()
            .id(scannerDomain.getId())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId())
            .tickers(scannerDomain.getTickers())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .spreadParam(algorithm.getSpreadParam())
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
    public SignalScanner toDomain(List<TradingSnapshot> instruments) {
        return SignalScanner.builder()
            .id(getId())
            .algorithm(
                PrefSimpleAlgorithmConfig
                    .builder()
                    .spreadParam(spreadParam)
                    .build()
                    .factoryAlgorithm()
            )
            .datasourceId(getDatasourceId())
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .tradingSnapshots(instruments)
            .lastExecutionDateTime(getLastExecutionDateTime())
            .signals(getSignals().stream().map(SignalEntity::toDomain).toList())
            .build();
    }

    @Override
    public void updateAlgorithmConfig(AlgorithmConfig config) {
        PrefSimpleAlgorithmConfig algorithmConfig = (PrefSimpleAlgorithmConfig) config;
        this.spreadParam = algorithmConfig.getSpreadParam();
    }
}
