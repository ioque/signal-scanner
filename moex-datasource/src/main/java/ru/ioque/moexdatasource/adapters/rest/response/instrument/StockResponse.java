package ru.ioque.moexdatasource.adapters.rest.response.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.instrument.Stock;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockResponse extends InstrumentResponse {
    Integer lotSize;
    String isin;
    String regNumber;
    Integer listLevel;

    @Builder
    public StockResponse(
        String ticker,
        String shortName,
        String name,
        Integer lotSize,
        String isin,
        String regNumber,
        Integer listLevel
    ) {
        super(ticker, shortName, name);
        this.lotSize = lotSize;
        this.isin = isin;
        this.regNumber = regNumber;
        this.listLevel = listLevel;
    }

    public static StockResponse from(Stock instrument) {
        return StockResponse.builder()
            .ticker(instrument.getTicker())
            .shortName(instrument.getShortName())
            .name(instrument.getName())
            .lotSize(instrument.getLotSize())
            .isin(instrument.getIsin())
            .regNumber(instrument.getRegNumber())
            .listLevel(instrument.getListLevel())
            .build();
    }
}
