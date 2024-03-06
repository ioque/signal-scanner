package ru.ioque.investfund.domain.statistic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentStatistic {
    UUID instrumentId;
    Double todayValue;
    Double historyMedianValue;
    Double todayLastPrice;
    Double todayOpenPrice;
    Double buyToSellValuesRatio;
}
