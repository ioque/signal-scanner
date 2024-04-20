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
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;

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

    public static ScannerEntity from(SignalScanner scannerDomain) {
        PrefCommonProperties properties = (PrefCommonProperties) scannerDomain.getProperties();
        PrefSimpleScannerEntity scannerEntity = PrefSimpleScannerEntity.builder()
            .id(scannerDomain.getId().getUuid())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId().getUuid())
            .tickers(scannerDomain.getInstrumentIds().stream().map(InstrumentId::getTicker).map(Ticker::getValue).toList())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .spreadParam(properties.getSpreadValue())
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
            .id(ScannerId.from(getId()))
            .properties(
                PrefCommonProperties
                    .builder()
                    .spreadValue(spreadParam)
                    .build()
            )
            .datasourceId(DatasourceId.from(getDatasourceId()))
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .instrumentIds(getTickers().stream().map(ticker -> new InstrumentId(new Ticker(ticker))).toList())
            .lastExecutionDateTime(getLastExecutionDateTime())
            .signals(getSignals().stream().map(SignalEntity::toDomain).collect(Collectors.toCollection(ArrayList::new)))
            .build();
    }
}
