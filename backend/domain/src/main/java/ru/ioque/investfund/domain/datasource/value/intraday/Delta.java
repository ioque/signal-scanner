package ru.ioque.investfund.domain.datasource.value.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.Instant;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delta extends IntradayData {
    @Builder
    public Delta(
        Ticker ticker,
        Long number,
        Instant timestamp,
        Double price,
        Double value
    ) {
        super(ticker, number, timestamp, price, value);
    }
}
