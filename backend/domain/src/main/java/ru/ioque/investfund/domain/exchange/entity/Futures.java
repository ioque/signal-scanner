package ru.ioque.investfund.domain.exchange.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.statistic.TimeSeriesValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Futures extends Instrument {
    Integer lotVolume;
    Double initialMargin;
    Double highLimit;
    Double lowLimit;
    String assetCode;

    @Builder
    public Futures(
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        String assetCode,
        List<IntradayValue> intradayValues,
        List<DailyValue> dailyValues
    ) {
        super(id, ticker, shortName, name, updatable, intradayValues, dailyValues);
        this.lotVolume = lotVolume;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.initialMargin = initialMargin;
        this.assetCode = assetCode;
    }

    @Override
    public List<TimeSeriesValue<Double, ChronoLocalDate>> getWaPriceSeries() {
        return List.of();
    }

    @Override
    public Double getTodayValue() {
        return super.getTodayValue() * lotVolume;
    }

    @Override
    public Double getBuyToSellValueRatio() {
        return 1D;
    }
}
