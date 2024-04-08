package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Stock extends Instrument {
    Integer lotSize;
    String isin;
    String regNumber;
    Integer listLevel;

    @Builder
    public Stock(
        UUID datasourceId,
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotSize,
        String isin,
        String regNumber,
        Integer listLevel,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id, ticker, shortName, name, updatable, lastHistoryDate, lastTradingNumber);
        this.lotSize = lotSize;
        this.isin = isin;
        this.regNumber = regNumber;
        this.listLevel = listLevel;
    }
}
