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
public class Futures extends Instrument {
    Integer lotVolume;
    Double initialMargin;
    Double highLimit;
    Double lowLimit;
    String assetCode;

    @Builder
    public Futures(
        UUID id,
        UUID datasourceId,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        String assetCode,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id, datasourceId, ticker, shortName, name, updatable, lastHistoryDate, lastTradingNumber);
        setLotVolume(lotVolume);
        setAssetCode(assetCode);
        setInitialMargin(initialMargin);
        setHighLimit(highLimit);
        setLowLimit(lowLimit);
    }

    private void setLotVolume(Integer lotVolume) {
        this.lotVolume = lotVolume;
    }

    private void setInitialMargin(Double initialMargin) {
        this.initialMargin = initialMargin;
    }

    private void setHighLimit(Double highLimit) {
        this.highLimit = highLimit;
    }

    private void setLowLimit(Double lowLimit) {
        this.lowLimit = lowLimit;
    }

    private void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }
}
