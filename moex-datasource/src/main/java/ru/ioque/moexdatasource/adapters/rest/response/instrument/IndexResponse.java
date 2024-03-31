package ru.ioque.moexdatasource.adapters.rest.response.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.instrument.Index;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IndexResponse extends InstrumentResponse {
    Double annualHigh;
    Double annualLow;

    @Builder
    public IndexResponse(String ticker, String shortName, String name, Double annualHigh, Double annualLow) {
        super(ticker, shortName, name);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }

    public static IndexResponse from(Index instrument) {
        return IndexResponse.builder()
            .ticker(instrument.getTicker())
            .shortName(instrument.getShortName())
            .name(instrument.getName())
            .annualHigh(instrument.getAnnualHigh())
            .annualHigh(instrument.getAnnualLow())
            .build();
    }
}
