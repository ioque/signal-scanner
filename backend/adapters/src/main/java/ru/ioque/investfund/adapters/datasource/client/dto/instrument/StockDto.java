package ru.ioque.investfund.adapters.datasource.client.dto.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.Stock;

import java.util.UUID;

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
    public Instrument toDomain(UUID id) {
        return Stock.builder()
            .id(id)
            .ticker(getTicker())
            .shortName(getShortName())
            .name(getName())
            .lotSize(lotSize)
            .isin(isin)
            .regNumber(regNumber)
            .listLevel(listLevel)
            .build();
    }
}
