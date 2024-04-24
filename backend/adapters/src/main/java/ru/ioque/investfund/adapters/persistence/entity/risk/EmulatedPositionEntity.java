package ru.ioque.investfund.adapters.persistence.entity.risk;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.UuidIdentity;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPositionId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "emulated_position", uniqueConstraints = { @UniqueConstraint(columnNames = { "instrument_id", "scanner_id" }) })
@Entity(name = "EmulatedPosition")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmulatedPositionEntity extends UuidIdentity {
    UUID scannerId;
    UUID instrumentId;
    Double openPrice;
    Double lastPrice;
    Double closePrice;
    Boolean isOpen;
    Double profit;

    @Builder
    public EmulatedPositionEntity(
        UUID id,
        UUID scannerId,
        UUID instrumentId,
        Double openPrice,
        Double lastPrice,
        Double closePrice,
        Boolean isOpen,
        Double profit
    ) {
        super(id);
        this.scannerId = scannerId;
        this.instrumentId = instrumentId;
        this.openPrice = openPrice;
        this.lastPrice = lastPrice;
        this.closePrice = closePrice;
        this.isOpen = isOpen;
        this.profit = profit;
    }

    public static EmulatedPositionEntity from(EmulatedPosition emulatedPosition) {
        return EmulatedPositionEntity.builder()
            .id(emulatedPosition.getId().getUuid())
            .scannerId(emulatedPosition.getScannerId().getUuid())
            .instrumentId(emulatedPosition.getInstrumentId().getUuid())
            .openPrice(emulatedPosition.getOpenPrice())
            .lastPrice(emulatedPosition.getLastPrice())
            .closePrice(emulatedPosition.getClosePrice())
            .isOpen(emulatedPosition.getIsOpen())
            .profit(emulatedPosition.getProfit())
            .build();
    }

    public EmulatedPosition toDomain() {
        return EmulatedPosition.builder()
            .id(EmulatedPositionId.from(this.getId()))
            .scannerId(ScannerId.from(this.getScannerId()))
            .instrumentId(InstrumentId.from(this.getInstrumentId()))
            .openPrice(this.getOpenPrice())
            .lastPrice(this.getLastPrice())
            .closePrice(this.getClosePrice())
            .isOpen(this.getIsOpen())
            .profit(this.getProfit())
            .build();
    }
}
