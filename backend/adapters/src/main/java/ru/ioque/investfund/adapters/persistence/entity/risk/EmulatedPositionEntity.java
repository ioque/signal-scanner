package ru.ioque.investfund.adapters.persistence.entity.risk;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;
import ru.ioque.investfund.domain.position.EmulatedPosition;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "emulated_position", uniqueConstraints = { @UniqueConstraint(columnNames = { "instrument_id", "scanner_id" }) })
@Entity(name = "EmulatedPosition")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmulatedPositionEntity extends UuidIdentity {
    @ManyToOne
    @JoinColumn(name = "scanner_id")
    ScannerEntity scanner;

    @ManyToOne
    @JoinColumn(name = "instrument_id")
    InstrumentEntity instrument;

    Double openPrice;

    Double lastPrice;

    Double closePrice;

    Boolean isOpen;

    Double profit;

    @Builder
    public EmulatedPositionEntity(
        UUID id,
        ScannerEntity scanner,
        InstrumentEntity instrument,
        Double openPrice,
        Double lastPrice,
        Double closePrice,
        Boolean isOpen,
        Double profit
    ) {
        super(id);
        this.scanner = scanner;
        this.instrument = instrument;
        this.openPrice = openPrice;
        this.lastPrice = lastPrice;
        this.closePrice = closePrice;
        this.isOpen = isOpen;
        this.profit = profit;
    }

    public static EmulatedPositionEntity from(EmulatedPosition emulatedPosition) {
        return EmulatedPositionEntity.builder()
            .id(emulatedPosition.getId().getUuid())
            .scanner(ScannerEntity.fromDomain(emulatedPosition.getScanner()))
            .instrument(InstrumentEntity.fromDomain(emulatedPosition.getInstrument()))
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
            .scanner(this.getScanner().toDomain())
            .instrument(this.getInstrument().toDomain())
            .openPrice(this.getOpenPrice())
            .lastPrice(this.getLastPrice())
            .closePrice(this.getClosePrice())
            .isOpen(this.getIsOpen())
            .profit(this.getProfit())
            .build();
    }
}
