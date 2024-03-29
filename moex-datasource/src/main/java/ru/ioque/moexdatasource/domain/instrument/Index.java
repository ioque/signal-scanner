package ru.ioque.moexdatasource.domain.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Index extends Instrument {
    Double annualHigh;
    Double annualLow;

    @Builder
    public Index(
        String ticker,
        String shortName,
        String name,
        String engine,
        String market,
        String board,
        Double annualHigh,
        Double annualLow
    ) {
        super(ticker, shortName, name, engine, market, board);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }
}
