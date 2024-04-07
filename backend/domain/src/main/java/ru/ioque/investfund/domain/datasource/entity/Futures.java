package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

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
        List<HistoryValue> historyValues
    ) {
        super(id, ticker, shortName, name, updatable, intradayValues, historyValues);
        this.lotVolume = lotVolume;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.initialMargin = initialMargin;
        this.assetCode = assetCode;
    }
}
