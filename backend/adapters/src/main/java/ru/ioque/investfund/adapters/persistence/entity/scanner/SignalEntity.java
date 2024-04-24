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
    LocalDateTime dateTime;
    Double price;
    String summary;
    boolean isOpen;

    @Builder
    public SignalEntity(
        ScannerEntity scanner,
        LocalDateTime dateTime,
        UUID instrumentId,
        Double price,
        String summary,
        boolean isBuy,
        boolean isOpen
    ) {
        this.id = new SignalPk(scanner.getId(), instrumentId, isBuy);
        this.scanner = scanner;
        this.dateTime = dateTime;
        this.price = price;
        this.summary = summary;
        this.isOpen = isOpen;
    }

    public Signal toDomain() {
        return Signal.builder()
            .watermark(getDateTime())
            .instrumentId(InstrumentId.from(getId().getInstrumentId()))
            .price(getPrice())
            .summary(getSummary())
            .isBuy(getId().isBuy)
            .isOpen(isOpen())
            .build();
    }

    public static SignalEntity from(ScannerEntity scanner, Signal signal) {
        return SignalEntity.builder()
            .scanner(scanner)
            .dateTime(signal.getWatermark())
            .price(signal.getPrice())
            .instrumentId(signal.getInstrumentId().getUuid())
            .summary(signal.getSummary())
            .isBuy(signal.isBuy())
            .isOpen(signal.isOpen())
            .build();
    }
}
