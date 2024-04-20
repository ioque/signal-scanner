package ru.ioque.apitest.kafka;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentId {
    Ticker ticker;

    public static InstrumentId from(Ticker ticker) {
        return new InstrumentId(ticker);
    }

    @Override
    public String toString() {
        return ticker.getValue();
    }
}
