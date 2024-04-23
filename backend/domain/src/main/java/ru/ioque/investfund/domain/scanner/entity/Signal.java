package ru.ioque.investfund.domain.scanner.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Signal {
    InstrumentId instrumentId;
    Double price;
    Ticker ticker;
    boolean isBuy;
    boolean isOpen;
    String summary;
    LocalDateTime watermark;

    public boolean sameByBusinessKey(Signal signal) {
        return signal.getTicker().equals(this.getTicker()) && signal.isBuy() == this.isBuy();
    }

    public void close() {
        this.isOpen = false;
    }

    public boolean sameByTicker(Signal signal) {
        return signal.getTicker().equals(this.getTicker());
    }

    public boolean isSell() {
        return !isBuy;
    }
}
