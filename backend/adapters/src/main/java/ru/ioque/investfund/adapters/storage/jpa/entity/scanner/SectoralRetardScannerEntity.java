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
import ru.ioque.investfund.domain.scanner.financial.algorithms.SectoralRetardSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("SectoralRetardScannerEntity")
public class SectoralRetardScannerEntity extends SignalScannerEntity {
    Double historyScale;
    Double intradayScale;

    @Builder
    public SectoralRetardScannerEntity(
        UUID id,
        String description,
        List<UUID> objectIds,
        Double historyScale,
        Double intradayScale
    ) {
        super(id, description, objectIds);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    public static SignalScannerEntity from(SignalScannerBot signalScannerBot) {
        SectoralRetardSignalConfig config = (SectoralRetardSignalConfig) signalScannerBot.getConfig();
        return SectoralRetardScannerEntity.builder()
            .id(signalScannerBot.getId())
            .description(signalScannerBot.getDescription())
            .objectIds(signalScannerBot.getObjectIds())
            .historyScale(config.getHistoryScale())
            .intradayScale(config.getIntradayScale())
            .build();
    }

    @Override
    public SignalScannerBot toDomain() {
        return new SignalScannerBot(
            getId(),
            getDescription(),
            getObjectIds(),
            new SectoralRetardSignalConfig(historyScale, intradayScale)
        );
    }
}
