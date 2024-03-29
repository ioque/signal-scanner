package ru.ioque.core.dataemulator.futures;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.core.dataemulator.core.DatasetValue;
import ru.ioque.core.dataemulator.core.DoubleValue;
import ru.ioque.core.dataemulator.core.IntegerValue;
import ru.ioque.core.dataemulator.core.IntradayValue;
import ru.ioque.core.dataemulator.core.LocalDateTimeValue;
import ru.ioque.core.dataemulator.core.LocalDateValue;
import ru.ioque.core.dataemulator.core.LocalTimeValue;
import ru.ioque.core.dataemulator.core.StringValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FuturesTrade extends IntradayValue {
    LocalDateValue tradeDate;
    IntegerValue quantity;
    IntegerValue recNo;
    @Builder
    public FuturesTrade(
        Integer tradeNo,
        String boardId,
        String secId,
        LocalDate tradeDate,
        LocalTime tradeTime,
        Double price,
        Integer quantity,
        LocalDateTime sysTime,
        Integer recNo
    ) {
        super(
            new StringValue("SECID", 3, secId),
            new IntegerValue("TRADENO", 1, tradeNo),
            new LocalDateTimeValue("SYSTIME", 8, sysTime),
            new LocalTimeValue("TRADETIME", 5, tradeTime),
            new StringValue("BOARDNAME", 2, boardId),
            new DoubleValue("PRICE", 6, price)
        );
        this.tradeDate = new LocalDateValue("TRADEDATE", 4, tradeDate);
        this.quantity = new IntegerValue("QUANTITY", 7, quantity);
        this.recNo = new IntegerValue("RECNO", 9, recNo);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            tradeNo,boardId,secId,tradeDate,tradeTime,price,quantity,sysTime,recNo
        );
    }
}
