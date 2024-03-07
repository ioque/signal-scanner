package ru.ioque.investfund.domain.scanner.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Signal {
    LocalDateTime dateTime;
    UUID instrumentId;
    boolean isBuy;

    public boolean sameByInstrumentId(Signal signal) {
        return signal.getInstrumentId().equals(instrumentId);
    }

    public boolean sameByIsBuy(Signal signal) {
        return signal.isBuy == this.isBuy;
    }
}
