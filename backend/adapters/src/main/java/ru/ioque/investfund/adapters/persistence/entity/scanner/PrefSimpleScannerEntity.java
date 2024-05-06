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
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerStatus;

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
        ScannerStatus status,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<UUID> instrumentIds,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double spreadParam
    ) {
        super(id, status, workPeriodInMinutes, description, datasourceId, instrumentIds, lastWorkDateTime, signals);
        this.spreadParam = spreadParam;
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
