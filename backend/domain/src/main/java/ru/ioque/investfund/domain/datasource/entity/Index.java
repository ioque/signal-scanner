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
public class Index extends Instrument {
    Double annualHigh;
    Double annualLow;

    @Builder
    public Index(
        UUID datasourceId,
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Double annualHigh,
        Double annualLow,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id, ticker, shortName, name, updatable, lastHistoryDate, lastTradingNumber);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }
}
