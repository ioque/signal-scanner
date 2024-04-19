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
import ru.ioque.investfund.adapters.persistence.entity.GeneratedIdEntity;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.Ticker;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "signal")
@Entity(name = "Signal")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalEntity extends GeneratedIdEntity {
    @ManyToOne
    @JoinColumn(name="scanner_id", nullable=false)
    ScannerEntity scanner;
    LocalDateTime dateTime;
    String ticker;
    Double price;
    String summary;
    boolean isBuy;
    boolean isOpen;

    public Signal toDomain() {
        return Signal.builder()
            .watermark(getDateTime())
            .instrumentId(InstrumentId.from(Ticker.from(getTicker())))
            .price(getPrice())
            .summary(getSummary())
            .isBuy(isBuy())
            .isOpen(isOpen())
            .build();
    }

    public static SignalEntity from(Signal signal) {
        return SignalEntity.builder()
            .dateTime(signal.getWatermark())
            .price(signal.getPrice())
            .ticker(signal.getInstrumentId().getTicker().getValue())
            .summary(signal.getSummary())
            .isBuy(signal.isBuy())
            .isOpen(signal.isOpen())
            .build();
    }

    @Builder
    public SignalEntity(
        Long id,
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
}
