package ru.ioque.investfund.adapters.persistence.entity.scanner;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.AbstractEntity;
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
public class SignalEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name="scanner_id", nullable=false)
    ScannerEntity scanner;
    LocalDateTime dateTime;
    String ticker;
    Double price;
    String summary;
    boolean isBuy;
    boolean isOpen;

    @Builder
    public SignalEntity(
        UUID id,
        ScannerEntity scanner,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        String summary,
        boolean isBuy,
        boolean isOpen
    ) {
        super(id);
        this.scanner = scanner;
        this.dateTime = dateTime;
        this.ticker = ticker;
        this.price = price;
        this.summary = summary;
        this.isBuy = isBuy;
        this.isOpen = isOpen;
    }

    public Signal toDomain() {
        return Signal.builder()
            .id(getId())
            .scannerId(getScanner().getId())
            .dateTime(getDateTime())
            .ticker(getTicker())
            .price(getPrice())
            .summary(getSummary())
            .isBuy(isBuy())
            .isOpen(isOpen())
            .build();
    }

    public static SignalEntity from(Signal signal) {
        return SignalEntity.builder()
            .id(signal.getId())
            .dateTime(signal.getDateTime())
            .price(signal.getPrice())
            .ticker(signal.getTicker())
            .summary(signal.getSummary())
            .isBuy(signal.isBuy())
            .isOpen(signal.isOpen())
            .build();
    }
}
