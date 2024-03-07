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
import ru.ioque.investfund.domain.scanner.financial.algorithms.PrefSimpleSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

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
        String description,
        List<UUID> objectIds,
        LocalDateTime lastWorkDateTime,
        List<SignalEntity> signals,
        Double spreadParam
    ) {
        super(id, description, objectIds, lastWorkDateTime, signals);
        this.spreadParam = spreadParam;
    }

    public static SignalScannerEntity from(SignalScannerBot signalScannerBot) {
        PrefSimpleSignalConfig config = (PrefSimpleSignalConfig) signalScannerBot.getConfig();
        return PrefSimpleScannerEntity.builder()
            .id(signalScannerBot.getId())
            .description(signalScannerBot.getDescription())
            .objectIds(signalScannerBot.getObjectIds())
            .lastWorkDateTime(signalScannerBot.getLastExecutionDateTime().orElse(null))
            .signals(signalScannerBot.getSignals().stream().map(SignalEntity::from).toList())
            .spreadParam(config.getSpreadParam())
            .build();
    }

    @Override
    public SignalScannerBot toDomain(List<FinInstrument> instruments) {
        return new SignalScannerBot(
            getId(),
            getDescription(),
            new PrefSimpleSignalConfig(getObjectIds(), spreadParam),
            getLastWorkDateTime(),
            getSignals().stream().map(SignalEntity::toDomain).toList(),
            instruments
        );
    }
}
