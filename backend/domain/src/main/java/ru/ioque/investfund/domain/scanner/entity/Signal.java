package ru.ioque.investfund.domain.scanner.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Signal {
    @Setter
    ScannerId scannerId;
    InstrumentId instrumentId;
    Double price;
    boolean isBuy;
    String summary;
    LocalDateTime watermark;

    public boolean sameByBusinessKey(Signal signal) {
        return signal.getInstrumentId().equals(this.getInstrumentId()) && signal.isBuy() == this.isBuy();
    }

    public boolean sameByTicker(Signal signal) {
        return signal.getInstrumentId().equals(this.getInstrumentId());
    }

    public boolean isSell() {
        return !isBuy;
    }
}
