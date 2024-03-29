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
public class Futures extends Instrument {
    Integer lotVolume;
    Double initialMargin;
    Double highLimit;
    Double lowLimit;
    String assetCode;

    @Builder
    public Futures(
        String ticker,
        String shortName,
        String name,
        String engine,
        String market,
        String board,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        String assetCode
    ) {
        super(ticker, shortName, name, engine, market, board);
        this.lotVolume = lotVolume;
        this.initialMargin = initialMargin;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.assetCode = assetCode;
    }
}
