package ru.ioque.moexdatasource.adapters.rest.response;

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
public class IndexResponse extends InstrumentResponse {
    Double annualHigh;
    Double annualLow;

    @Builder
    public IndexResponse(String ticker, String shortName, String name, Double annualHigh, Double annualLow) {
        super(ticker, shortName, name);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }
}
