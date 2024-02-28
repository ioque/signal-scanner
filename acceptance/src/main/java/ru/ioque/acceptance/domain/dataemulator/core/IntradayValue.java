package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class IntradayValue implements DatasetObject, Comparable<IntradayValue> {
    protected StringValue secId;
    protected IntegerValue tradeNo;
    protected LocalDateTimeValue sysTime;

    public boolean equalsBy(String secId) {
        return Objects.equals(this.secId.getValue(), secId);
    }

    @Override
    public int compareTo(IntradayValue intradayValue) {
        return tradeNo.compareTo(intradayValue.getTradeNo());
    }
}