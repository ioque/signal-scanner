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
public class CurrencyPair extends Instrument {
    Integer lotSize;
    String faceUnit;

    @Builder
    public CurrencyPair(
        UUID id,
        UUID datasourceId,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotSize,
        String faceUnit,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id, datasourceId, ticker, shortName, name, updatable, lastHistoryDate, lastTradingNumber);
        setFaceUnit(faceUnit);
        setLotSize(lotSize);
    }

    private void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    private void setFaceUnit(String faceUnit) {
        this.faceUnit = faceUnit;
    }
}
