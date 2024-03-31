package ru.ioque.moexdatasource.adapters.rest.response.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.instrument.CurrencyPair;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyPairResponse extends InstrumentResponse {
    Integer lotSize;
    String faceUnit;

    @Builder
    public CurrencyPairResponse(String ticker, String shortName, String name, Integer lotSize, String faceUnit) {
        super(ticker, shortName, name);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }

    public static CurrencyPairResponse from(CurrencyPair instrument) {
        return CurrencyPairResponse.builder()
            .ticker(instrument.getTicker())
            .shortName(instrument.getShortName())
            .name(instrument.getName())
            .lotSize(instrument.getLotSize())
            .faceUnit(instrument.getFaceUnit())
            .build();
    }
}
