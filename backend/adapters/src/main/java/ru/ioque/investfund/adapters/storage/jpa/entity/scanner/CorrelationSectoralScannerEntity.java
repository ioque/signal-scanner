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
import ru.ioque.investfund.domain.scanner.financial.algorithms.CorrelationSectoralSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("CorrelationSectoralScannerEntity")
public class CorrelationSectoralScannerEntity extends SignalScannerEntity {
    Double futuresOvernightScale;
    Double stockOvernightScale;
    String futuresTicker;

    @Builder
    public CorrelationSectoralScannerEntity(
        UUID id,
        String description,
        List<UUID> objectIds,
        Double futuresOvernightScale,
        Double stockOvernightScale,
        String futuresTicker
    ) {
        super(id, description, objectIds);
        this.futuresOvernightScale = futuresOvernightScale;
        this.stockOvernightScale = stockOvernightScale;
        this.futuresTicker = futuresTicker;
    }

    public static SignalScannerEntity from(SignalScannerBot signalScannerBot) {
        CorrelationSectoralSignalConfig config = (CorrelationSectoralSignalConfig) signalScannerBot.getConfig();
        return CorrelationSectoralScannerEntity.builder()
            .id(signalScannerBot.getId())
            .description(signalScannerBot.getDescription())
            .objectIds(signalScannerBot.getObjectIds())
            .futuresOvernightScale(config.getFuturesOvernightScale())
            .stockOvernightScale(config.getStockOvernightScale())
            .futuresTicker(config.getFuturesTicker())
            .build();
    }

    @Override
    public SignalScannerBot toDomain() {
        return new SignalScannerBot(
            getId(),
            getDescription(),
            getObjectIds(),
            new CorrelationSectoralSignalConfig(futuresOvernightScale, stockOvernightScale, futuresTicker)
        );
    }
}
