package ru.ioque.investfund.adapters.persistence.entity.scanner;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.UuidIdentity;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.algorithms.AlgorithmType;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.ScannerStatus;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "scanner")
@Entity(name = "ScannerEntity")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "SCANNER_TYPE", discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class ScannerEntity extends UuidIdentity {
    @Enumerated(EnumType.STRING)
    ScannerStatus status;
    Integer workPeriodInMinutes;
    String description;
    UUID datasourceId;
    @ElementCollection(fetch = FetchType.EAGER)
    List<UUID> instrumentIds;
    LocalDateTime lastExecutionDateTime;
    @OneToMany(mappedBy = "scanner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<SignalEntity> signals;

    public ScannerEntity(
        UUID id,
        ScannerStatus status,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<UUID> instrumentIds,
        LocalDateTime lastExecutionDateTime,
        List<SignalEntity> signals
    ) {
        super(id);
        this.status = status;
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.datasourceId = datasourceId;
        this.instrumentIds = instrumentIds;
        this.lastExecutionDateTime = lastExecutionDateTime;
        this.signals = signals;
    }

    public SignalScanner toDomain() {
        return SignalScanner.builder()
            .id(ScannerId.from(getId()))
            .status(status)
            .properties(getAlgorithmProperties())
            .datasourceId(DatasourceId.from(getDatasourceId()))
            .workPeriodInMinutes(getWorkPeriodInMinutes())
            .description(getDescription())
            .instrumentIds(getInstrumentIds().stream().map(InstrumentId::new).toList())
            .lastExecutionDateTime(getLastExecutionDateTime())
            .signals(getSignals().stream().map(SignalEntity::toDomain).collect(Collectors.toCollection(ArrayList::new)))
            .build();
    }

    public abstract AlgorithmProperties getAlgorithmProperties();

    public static ScannerEntity fromDomain(SignalScanner dataScanner) {
        return mappers.get(dataScanner.getProperties().getType()).apply(dataScanner);
    }

    private final static Map<AlgorithmType, Function<SignalScanner, ScannerEntity>> mappers = Map.of(
        AlgorithmType.ANOMALY_VOLUME, ScannerEntity::toAnomalyVolumeScanner,
        AlgorithmType.SECTORAL_RETARD, ScannerEntity::toSectoralRetardScanner,
        AlgorithmType.SECTORAL_FUTURES, ScannerEntity::toSectoralFuturesScanner,
        AlgorithmType.PREF_COMMON, ScannerEntity::toPrefSimpleScanner
    );

    public static ScannerEntity toAnomalyVolumeScanner(SignalScanner scannerDomain) {
        AnomalyVolumeProperties properties = (AnomalyVolumeProperties) scannerDomain.getProperties();
        AnomalyVolumeScannerEntity scannerEntity = AnomalyVolumeScannerEntity.builder()
            .id(scannerDomain.getId().getUuid())
            .status(scannerDomain.getStatus())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId().getUuid())
            .instrumentIds(scannerDomain.getInstrumentIds().stream().map(InstrumentId::getUuid).toList())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .scaleCoefficient(properties.getScaleCoefficient())
            .historyPeriod(properties.getHistoryPeriod())
            .indexTicker(properties.getIndexTicker().getValue())
            .build();
        List<SignalEntity> signals = scannerDomain
            .getSignals()
            .stream()
            .map(signal -> SignalEntity.from(scannerEntity, signal))
            .toList();
        scannerEntity.setSignals(signals);
        return scannerEntity;
    }

    public static ScannerEntity toSectoralRetardScanner(SignalScanner scannerDomain) {
        SectoralRetardProperties properties = (SectoralRetardProperties) scannerDomain.getProperties();
        SectoralRetardScannerEntity scannerEntity = SectoralRetardScannerEntity.builder()
            .id(scannerDomain.getId().getUuid())
            .status(scannerDomain.getStatus())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId().getUuid())
            .instrumentIds(scannerDomain.getInstrumentIds().stream().map(InstrumentId::getUuid).toList())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .historyScale(properties.getHistoryScale())
            .intradayScale(properties.getIntradayScale())
            .build();
        List<SignalEntity> signals = scannerDomain
            .getSignals()
            .stream()
            .map(signal -> SignalEntity.from(scannerEntity, signal))
            .toList();
        scannerEntity.setSignals(signals);
        return scannerEntity;
    }

    public static ScannerEntity toSectoralFuturesScanner(SignalScanner scannerDomain) {
        SectoralFuturesProperties properties = (SectoralFuturesProperties) scannerDomain.getProperties();
        SectoralFuturesScannerEntity scannerEntity = SectoralFuturesScannerEntity.builder()
            .id(scannerDomain.getId().getUuid())
            .status(scannerDomain.getStatus())
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
            .map(signal -> SignalEntity.from(scannerEntity, signal))
            .toList();
        scannerEntity.setSignals(signals);
        return scannerEntity;
    }

    public static ScannerEntity toPrefSimpleScanner(SignalScanner scannerDomain) {
        PrefCommonProperties properties = (PrefCommonProperties) scannerDomain.getProperties();
        PrefSimpleScannerEntity scannerEntity = PrefSimpleScannerEntity.builder()
            .id(scannerDomain.getId().getUuid())
            .status(scannerDomain.getStatus())
            .workPeriodInMinutes(scannerDomain.getWorkPeriodInMinutes())
            .description(scannerDomain.getDescription())
            .datasourceId(scannerDomain.getDatasourceId().getUuid())
            .instrumentIds(scannerDomain.getInstrumentIds().stream().map(InstrumentId::getUuid).toList())
            .lastWorkDateTime(scannerDomain.getLastExecutionDateTime().orElse(null))
            .spreadParam(properties.getSpreadValue())
            .build();
        List<SignalEntity> signals = scannerDomain
            .getSignals()
            .stream()
            .map(signal -> SignalEntity.from(scannerEntity, signal))
            .toList();
        scannerEntity.setSignals(signals);
        return scannerEntity;
    }
}
