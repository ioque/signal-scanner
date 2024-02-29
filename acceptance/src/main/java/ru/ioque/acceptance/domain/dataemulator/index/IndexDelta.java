package ru.ioque.acceptance.domain.dataemulator.index;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.domain.dataemulator.core.DatasetValue;
import ru.ioque.acceptance.domain.dataemulator.core.DoubleValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntegerValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntradayValue;
import ru.ioque.acceptance.domain.dataemulator.core.LocalDateTimeValue;
import ru.ioque.acceptance.domain.dataemulator.core.LocalDateValue;
import ru.ioque.acceptance.domain.dataemulator.core.LocalTimeValue;
import ru.ioque.acceptance.domain.dataemulator.core.StringValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IndexDelta extends IntradayValue {
    LocalDateValue tradeDate;
    DoubleValue value;
    IntegerValue decimals;
    @Builder
    public IndexDelta(
        Integer tradeNo,
        String boardId,
        String secId,
        LocalDate tradeDate,
        LocalTime tradeTime,
        Double price,
        Double value,
        LocalDateTime sysTime,
        Integer decimals
    ) {
        super(
            new StringValue("SECID", 3, secId),
            new IntegerValue("TRADENO", 1, tradeNo),
            new LocalDateTimeValue("SYSTIME", 8, sysTime),
            new LocalTimeValue("TRADETIME", 5, tradeTime),
            new StringValue("BOARDID", 2, boardId),
            new DoubleValue("PRICE", 6, price)
        );
        this.tradeDate = new LocalDateValue("TRADEDATE", 4, tradeDate);
        this.value = new DoubleValue("VALUE", 7, value);
        this.decimals = new IntegerValue("DECIMALS", 9, decimals);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            tradeNo, boardId,secId,tradeDate,tradeTime,price,value,sysTime,decimals
        );
    }
}
