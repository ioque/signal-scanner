package ru.ioque.investfund.adapters.datasource.client.dto.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.details.StockDetails;
import ru.ioque.investfund.domain.datasource.value.types.Isin;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockDto extends InstrumentDto {
    Integer lotSize;
    String isin;
    String regNumber;
    Integer listLevel;

    @Builder
    public StockDto(
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

    @Override
    public InstrumentDetails toDetails() {
        return StockDetails.builder()
            .ticker(new Ticker(getTicker()))
            .shortName(getShortName())
            .name(getName())
            .lotSize(lotSize)
            .isin(Isin.from(isin))
            .regNumber(regNumber)
            .listLevel(listLevel)
            .build();
    }
}
