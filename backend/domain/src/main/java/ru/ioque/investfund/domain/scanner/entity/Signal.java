package ru.ioque.investfund.domain.scanner.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.value.SignalSign;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Signal {
    UUID id;
    UUID scannerId;
    LocalDateTime dateTime;
    String ticker;
    Double price;
    String summary;
    boolean isBuy;
    boolean isOpen;

    public static Signal of(UUID id, UUID scannerId, SignalSign signalSign) {
        return Signal.builder()
            .id(id)
            .scannerId(scannerId)
            .dateTime(signalSign.getDateTime())
            .price(signalSign.getPrice())
            .ticker(signalSign.getTicker())
            .summary(signalSign.getSummary())
            .isBuy(signalSign.isBuy())
            .isOpen(true)
            .build();
    }

    public void close() {
        this.isOpen = false;
    }

    public boolean sameByTicker(Signal signal) {
        return signal.getTicker().equals(this.getTicker());
    }
}
