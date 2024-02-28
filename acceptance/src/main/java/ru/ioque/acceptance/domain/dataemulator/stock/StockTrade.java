package ru.ioque.acceptance.domain.dataemulator.stock;

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
import ru.ioque.acceptance.domain.dataemulator.core.LocalTimeValue;
import ru.ioque.acceptance.domain.dataemulator.core.StringValue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockTrade extends IntradayValue {
    LocalTimeValue tradeTime;
    StringValue boardId;
    DoubleValue price;
    IntegerValue quantity;
    DoubleValue value;
    StringValue period;
    IntegerValue tradeTimeGrp;
    StringValue buySell;
    IntegerValue decimals;
    StringValue tradingSession;

    @Builder
    public StockTrade(
        Integer tradeNo,
        LocalTime tradeTime,
        String boardId,
        String secId,
        Double price,
        Integer quantity,
        Double value,
        String period,
        Integer tradeTimeGrp,
        LocalDateTime sysTime,
        String buySell,
        Integer decimals,
        String tradingSession
    ) {
        super(
            new StringValue("SECID", 4, secId),
            new IntegerValue("TRADENO", 1, tradeNo),
            new LocalDateTimeValue("SYSTIME", 10, sysTime)
        );
        this.tradeTime = new LocalTimeValue("TRADETIME", 2, tradeTime);
        this.boardId = new StringValue("BOARDID", 3, boardId);
        this.price = new DoubleValue("PRICE", 5, price);
        this.quantity = new IntegerValue("QUANTITY", 6, quantity);
        this.value = new DoubleValue("VALUE", 7, value);
        this.period = new StringValue("PERIOD", 8, period);
        this.tradeTimeGrp = new IntegerValue("TRADETIME_GRP", 9, tradeTimeGrp);
        this.buySell = new StringValue("BUYSELL", 11, buySell);
        this.decimals = new IntegerValue("DECIMALS", 12, decimals);
        this.tradingSession = new StringValue("TRADINGSESSION", 13, tradingSession);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            tradeNo,tradeTime,boardId,secId,price,quantity,value,period,tradeTimeGrp,sysTime,buySell,decimals,tradingSession
        );
    }
}
