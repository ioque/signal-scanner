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
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;

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
@DiscriminatorValue("CorrelationSectoralScannerEntity")
public class SectoralFuturesScannerEntity extends ScannerEntity {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;

    @Builder
    public SectoralFuturesScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<UUID> instrumentIds,
        List<SignalEntity> signals,
        LocalDateTime lastWorkDateTime,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(id, workPeriodInMinutes, description, datasourceId, instrumentIds, lastWorkDateTime, signals);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    public static ScannerEntity from(SignalScanner scannerDomain) {
        SectoralFuturesProperties properties = (SectoralFuturesProperties) scannerDomain.getProperties();
        SectoralFuturesScannerEntity scannerEntity = SectoralFuturesScannerEntity.builder()
            .id(scannerDomain.getId().getUuid())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId().getUuid())
            .instrumentIds(scannerDomain.getInstrumentIds().stream().map(InstrumentId::getUuid).toList())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .futuresOvernightScale(properties.getFuturesOvernightScale())
            .stockOvernightScale(properties.getStockOvernightScale())
            .futuresTicker(properties.getFuturesTicker().getValue())
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
                SectoralFuturesProperties
                    .builder()
                    .futuresOvernightScale(futuresOvernightScale)
                    .stockOvernightScale(stockOvernightScale)
                    .futuresTicker(new Ticker(futuresTicker))
                    .build()
            )
            .datasourceId(DatasourceId.from(getDatasourceId()))
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .instrumentIds(getInstrumentIds().stream().map(InstrumentId::new).toList())
            .lastExecutionDateTime(getLastExecutionDateTime())
            .signals(getSignals().stream().map(SignalEntity::toDomain).collect(Collectors.toCollection(ArrayList::new)))
            .build();
    }
}
