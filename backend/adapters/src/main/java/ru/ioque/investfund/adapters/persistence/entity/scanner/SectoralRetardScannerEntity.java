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
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;

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

    public static ScannerEntity from(SignalScanner scannerDomain) {
        SectoralRetardProperties properties = (SectoralRetardProperties) scannerDomain.getProperties();
        SectoralRetardScannerEntity scannerEntity = SectoralRetardScannerEntity.builder()
            .id(scannerDomain.getId().getUuid())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId().getUuid())
            .tickers(scannerDomain.getInstrumentIds().stream().map(InstrumentId::getTicker).map(Ticker::getValue).toList())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .historyScale(properties.getHistoryScale())
            .intradayScale(properties.getIntradayScale())
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
                SectoralRetardProperties
                    .builder()
                    .historyScale(historyScale)
                    .intradayScale(intradayScale)
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