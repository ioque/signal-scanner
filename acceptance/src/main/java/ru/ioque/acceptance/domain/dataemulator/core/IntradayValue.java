package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class IntradayValue implements DatasetObject, Comparable<IntradayValue> {
    protected IntegerValue tradeNo;
    protected LocalDateTimeValue sysTime;

    public boolean isBeforeOrEqual(LocalDateTime dateTime) {
        var sysTime = ((LocalDateTime) this.sysTime.getValue());
        return sysTime.isBefore(dateTime) || sysTime.isEqual(dateTime);
    }

    @Override
    public int compareTo(IntradayValue intradayValue) {
        return tradeNo.compareTo(intradayValue.getTradeNo());
    }
}