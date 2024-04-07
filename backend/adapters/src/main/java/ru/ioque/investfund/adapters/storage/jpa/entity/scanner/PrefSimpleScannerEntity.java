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
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.entity.algorithms.prefsimplepair.PrefSimpleAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.algorithms.prefsimplepair.PrefSimpleAlgorithmConfigurator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("PrefSimpleScannerEntity")
public class PrefSimpleScannerEntity extends SignalScannerEntity {
    Double spreadParam;

    @Builder
    public PrefSimpleScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        List<String> tickers,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double spreadParam
    ) {
        super(id, workPeriodInMinutes, description, tickers, lastWorkDateTime, signals);
        this.spreadParam = spreadParam;
    }

    public static SignalScannerEntity from(SignalScanner signalScanner) {
        PrefSimpleAlgorithm algorithm = (PrefSimpleAlgorithm) signalScanner.getAlgorithm();
        return PrefSimpleScannerEntity.builder()
            .id(signalScanner.getId())
            .workPeriodInMinutes(signalScanner.getWorkPeriodInMinutes())
            .description(signalScanner.getDescription())
            .tickers(signalScanner.getTickers())
            .lastWorkDateTime(signalScanner.getLastExecutionDateTime().orElse(null))
            .signals(signalScanner.getSignals().stream().map(SignalEntity::from).toList())
            .spreadParam(algorithm.getSpreadParam())
            .build();
    }

    @Override
    public SignalScanner toDomain(List<TradingSnapshot> instruments) {
        return SignalScanner.builder()
            .id(getId())
            .algorithm(
                PrefSimpleAlgorithmConfigurator
                    .builder()
                    .spreadParam(spreadParam)
                    .build()
                    .factoryAlgorithm()
            )
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .tradingSnapshots(instruments)
            .lastExecutionDateTime(getLastExecutionDateTime())
            .signals(getSignals().stream().map(SignalEntity::toDomain).toList())
            .build();
    }
}
