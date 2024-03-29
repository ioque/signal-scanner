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
public class CurrencyPair extends Instrument {
    Integer lotSize;
    String faceUnit;

    @Builder
    public CurrencyPair(
        String ticker,
        String shortName,
        String name,
        String engine,
        String market,
        String board,
        Integer lotSize,
        String faceUnit
    ) {
        super(ticker, shortName, name, engine, market, board);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }
}
