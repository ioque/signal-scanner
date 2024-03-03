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
import ru.ioque.investfund.domain.scanner.financial.algorithms.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("AnomalyVolumeScannerEntity")
public class AnomalyVolumeScannerEntity extends SignalScannerEntity {
    Double scaleCoefficient;
    Integer historyPeriod;
    String indexTicker;

    @Builder
    public AnomalyVolumeScannerEntity(
        UUID id,
        String description,
        List<UUID> objectIds,
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        super(id, description, objectIds);
        this.scaleCoefficient = scaleCoefficient;
        this.historyPeriod = historyPeriod;
        this.indexTicker = indexTicker;
    }

    public static SignalScannerEntity from(SignalScannerBot signalScannerBot) {
        AnomalyVolumeSignalConfig config = (AnomalyVolumeSignalConfig) signalScannerBot.getConfig();
        return AnomalyVolumeScannerEntity.builder()
            .id(signalScannerBot.getId())
            .description(signalScannerBot.getDescription())
            .objectIds(signalScannerBot.getObjectIds())
            .scaleCoefficient(config.getScaleCoefficient())
            .historyPeriod(config.getHistoryPeriod())
            .indexTicker(config.getIndexTicker())
            .build();
    }

    @Override
    public SignalScannerBot toDomain() {
        return new SignalScannerBot(
            getId(),
            getDescription(),
            getObjectIds(),
            new AnomalyVolumeSignalConfig(scaleCoefficient, historyPeriod, indexTicker)
        );
    }
}
