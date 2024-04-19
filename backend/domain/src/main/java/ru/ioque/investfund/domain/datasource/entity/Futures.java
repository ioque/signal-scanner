package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

import java.time.LocalDate;

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
        InstrumentId id,
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
        super(id, shortName, name, updatable, lastHistoryDate, lastTradingNumber);
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
