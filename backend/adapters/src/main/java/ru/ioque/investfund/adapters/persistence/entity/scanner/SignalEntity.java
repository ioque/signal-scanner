package ru.ioque.investfund.adapters.persistence.entity.scanner;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "signal")
@Entity(name = "Signal")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalEntity {
    @EmbeddedId
    SignalPk id;

    @ManyToOne
    @MapsId("scannerId")
    @JoinColumn(name="scanner_id", nullable=false)
    ScannerEntity scanner;

    Double price;
    boolean isBuy;
    String summary;
    LocalDateTime dateTime;

    @Builder
    public SignalEntity(
        ScannerEntity scanner,
        LocalDateTime dateTime,
        UUID instrumentId,
        Double price,
        String summary,
        boolean isBuy
    ) {
        this.id = new SignalPk(scanner.getId(), instrumentId);
        this.isBuy = isBuy;
        this.price = price;
        this.summary = summary;
        this.scanner = scanner;
        this.dateTime = dateTime;
    }

    public Signal toDomain() {
        return Signal.builder()
            .openPrice(getPrice())
            .summary(getSummary())
            .instrumentId(InstrumentId.from(getId().getInstrumentId()))
            .build();
    }

    public static SignalEntity from(ScannerEntity scanner, Signal signal) {
        return SignalEntity.builder()
            .scanner(scanner)
            .price(signal.getOpenPrice())
            .summary(signal.getSummary())
            .instrumentId(signal.getInstrumentId().getUuid())
            .build();
    }
}
