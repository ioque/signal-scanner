package ru.ioque.moexdatasource.adapters.rest.response.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.instrument.Futures;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FuturesResponse extends InstrumentResponse {
    Integer lotVolume;
    Double initialMargin;
    Double highLimit;
    Double lowLimit;
    String assetCode;

    @Builder
    public FuturesResponse(
        String ticker,
        String shortName,
        String name,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        String assetCode
    ) {
        super(ticker, shortName, name);
        this.lotVolume = lotVolume;
        this.initialMargin = initialMargin;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.assetCode = assetCode;
    }

    public static FuturesResponse from(Futures instrument) {
        return FuturesResponse.builder()
            .ticker(instrument.getTicker())
            .shortName(instrument.getShortName())
            .name(instrument.getName())
            .lotVolume(instrument.getLotVolume())
            .initialMargin(instrument.getInitialMargin())
            .highLimit(instrument.getHighLimit())
            .lowLimit(instrument.getLowLimit())
            .assetCode(instrument.getAssetCode())
            .build();
    }
}
