package ru.ioque.investfund.domain.scanner.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainEvent;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalFoundEvent implements DomainEvent {
    InstrumentId instrumentId;
    Double price;
    boolean isBuy;
    ScannerId scannerId;
    LocalDateTime watermark;

    public static DomainEvent of(ScannerId scannerId, Signal newSignal) {
        return SignalFoundEvent.builder()
            .instrumentId(newSignal.getInstrumentId())
            .price(newSignal.getPrice())
            .isBuy(newSignal.isBuy())
            .scannerId(scannerId)
            .watermark(newSignal.getWatermark())
            .build();
    }
}
