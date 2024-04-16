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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Stock extends Instrument {
    Integer lotSize;
    String isin;
    String regNumber;
    Integer listLevel;

    @Builder
    public Stock(
        UUID id,
        UUID datasourceId,
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
        super(id, datasourceId, ticker, shortName, name, updatable, lastHistoryDate, lastTradingNumber);
        setLotSize(lotSize);
        setIsin(isin);
        setRegNumber(regNumber);
        setListLevel(listLevel);
    }

    private void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    private void setIsin(String isin) {
        this.isin = isin;
    }

    private void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    private void setListLevel(Integer listLevel) {
        this.listLevel = listLevel;
    }
}
