package ru.ioque.investfund.adapters.persistence.entity.scanner;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

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
public class PrefSimpleScannerEntity extends ScannerEntity {
    Double spreadParam;

    @Builder
    public PrefSimpleScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<UUID> instrumentIds,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double spreadParam
    ) {
        super(id, workPeriodInMinutes, description, datasourceId, instrumentIds, lastWorkDateTime, signals);
        this.spreadParam = spreadParam;
    }

    public static ScannerEntity from(SignalScanner scannerDomain) {
        PrefCommonProperties properties = (PrefCommonProperties) scannerDomain.getProperties();
        PrefSimpleScannerEntity scannerEntity = PrefSimpleScannerEntity.builder()
            .id(scannerDomain.getId().getUuid())
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

    @Override
    @Transient
    public AlgorithmProperties getAlgorithmProperties() {
        return PrefCommonProperties
            .builder()
            .spreadValue(spreadParam)
            .build();
    }
}
