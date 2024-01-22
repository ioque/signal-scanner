package ru.ioque.investfund.adapters.exchagne.moex.client.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
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
            .ticker(ticker)
            .shortName(shortName)
            .name(name)
            .lotSize(lotSize)
            .isin(isin)
            .regNumber(regNumber)
            .listLevel(listLevel)
            .dailyValues(new ArrayList<>())
            .intradayValues(new ArrayList<>())
            .build();
    }
}
