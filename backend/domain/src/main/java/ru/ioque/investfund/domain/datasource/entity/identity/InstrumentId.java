package ru.ioque.investfund.domain.datasource.entity.identity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.Ticker;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentId {
    Ticker ticker;

    public static InstrumentId from(Ticker ticker) {
        return new InstrumentId(ticker);
    }
}
