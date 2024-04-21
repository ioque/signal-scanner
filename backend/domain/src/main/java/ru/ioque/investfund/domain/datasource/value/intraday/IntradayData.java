package ru.ioque.investfund.domain.datasource.value.intraday;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class IntradayData implements Comparable<IntradayData> {
    Ticker ticker;
    Long number;
    LocalDateTime dateTime;
    Double price;
    Double value;

    @Override
    public int compareTo(IntradayData intradayData) {
        return Objects.compare(getNumber(), intradayData.getNumber(), Long::compareTo);
    }
}
