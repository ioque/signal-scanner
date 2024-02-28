package ru.ioque.acceptance.domain.dataemulator.futures;

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
public class FuturesTrade extends IntradayValue {
    StringValue boardName;
    LocalDateValue tradeDate;
    LocalTimeValue tradeTime;
    DoubleValue price;
    IntegerValue quantity;
    IntegerValue recNo;

    @Builder
    public FuturesTrade(
        Integer tradeNo,
        String boardName,
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
            new LocalDateTimeValue("SYSTIME", 8, sysTime)
        );
        this.boardName = new StringValue("BOARDNAME", 2, boardName);
        this.tradeDate = new LocalDateValue("TRADEDATE", 4, tradeDate);
        this.tradeTime = new LocalTimeValue("TRADETIME", 5, tradeTime);
        this.price = new DoubleValue("PRICE", 6, price);
        this.quantity = new IntegerValue("QUANTITY", 7, quantity);
        this.recNo = new IntegerValue("RECNO", 9, recNo);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            tradeNo, boardName,secId,tradeDate,tradeTime,price,quantity,sysTime,recNo
        );
    }
}
