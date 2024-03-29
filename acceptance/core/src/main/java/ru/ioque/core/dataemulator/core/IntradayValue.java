package ru.ioque.core.dataemulator.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class IntradayValue implements DatasetObject, Comparable<IntradayValue> {
    StringValue secId;
    IntegerValue tradeNo;
    LocalDateTimeValue sysTime;
    LocalTimeValue tradeTime;
    StringValue boardId;
    DoubleValue price;

    public boolean equalsBy(String secId) {
        return Objects.equals(this.secId.getValue(), secId);
    }

    @Override
    public int compareTo(IntradayValue intradayValue) {
        return tradeNo.compareTo(intradayValue.getTradeNo());
    }
}