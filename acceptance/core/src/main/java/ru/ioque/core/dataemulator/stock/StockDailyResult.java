package ru.ioque.core.dataemulator.stock;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.core.dataemulator.core.DailyResultValue;
import ru.ioque.core.dataemulator.core.DatasetValue;
import ru.ioque.core.dataemulator.core.DoubleValue;
import ru.ioque.core.dataemulator.core.IntegerValue;
import ru.ioque.core.dataemulator.core.LocalDateValue;
import ru.ioque.core.dataemulator.core.StringValue;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockDailyResult extends DailyResultValue {
    StringValue shortName;
    DoubleValue numTrades;
    DoubleValue value;
    DoubleValue legalClosePrice;
    DoubleValue waPrice;
    DoubleValue volume;
    DoubleValue marketPrice2;
    DoubleValue marketPrice3;
    DoubleValue admittedQuote;
    DoubleValue mp2ValTrd;
    DoubleValue marketPrice3TradesValue;
    DoubleValue admittedValue;
    DoubleValue waVal;
    IntegerValue tradingSession;
    StringValue currencyId;
    DoubleValue trendClsPr;

    @Builder
    public StockDailyResult(
        String boardId,
        LocalDate tradeDate,
        String shortName,
        String secId,
        Double numTrades,
        Double value,
        Double open,
        Double low,
        Double high,
        Double legalClosePrice,
        Double waPrice,
        Double close,
        Double volume,
        Double marketPrice2,
        Double marketPrice3,
        Double admittedQuote,
        Double mp2ValTrd,
        Double marketPrice3TradesValue,
        Double admittedValue,
        Double waVal,
        Integer tradingSession,
        String currencyId,
        Double trendClsPr
    ) {
        super(
            new StringValue("SECID", 4, secId),
            new LocalDateValue("TRADEDATE", 2, tradeDate),
            new StringValue("BOARDID", 1, boardId),
            new DoubleValue("OPEN", 7, open),
            new DoubleValue("LOW", 8, low),
            new DoubleValue("HIGH", 9, high),
            new DoubleValue("CLOSE", 12, close)
        );
        this.shortName = new StringValue("SHORTNAME", 3, shortName);
        this.numTrades = new DoubleValue("NUMTRADES", 5, numTrades);
        this.value = new DoubleValue("VALUE", 6, value);
        this.legalClosePrice = new DoubleValue("LEGALCLOSEPRICE", 10, legalClosePrice);
        this.waPrice = new DoubleValue("WAPRICE", 11, waPrice);
        this.volume = new DoubleValue("VOLUME", 13, volume);
        this.marketPrice2 = new DoubleValue("MARKETPRICE2", 14, marketPrice2);
        this.marketPrice3 = new DoubleValue("MARKETPRICE3", 15, marketPrice3);
        this.admittedQuote = new DoubleValue("ADMITTEDQUOTE", 16, admittedQuote);
        this.mp2ValTrd = new DoubleValue("MP2VALTRD", 17, mp2ValTrd);
        this.marketPrice3TradesValue = new DoubleValue("MARKETPRICE3TRADESVALUE", 18, marketPrice3TradesValue);
        this.admittedValue = new DoubleValue("ADMITTEDVALUE", 19, admittedValue);
        this.waVal = new DoubleValue("WAVAL", 20, waVal);
        this.tradingSession = new IntegerValue("TRADINGSESSION", 21, tradingSession);
        this.currencyId = new StringValue("CURRENCYID", 22, currencyId);
        this.trendClsPr = new DoubleValue("TRENDCLSPR", 23, trendClsPr);
    }

    public List<? extends DatasetValue> getRow() {
        return List.of(
            boardId,tradeDate,shortName,secId,numTrades,value,open,low,high,legalClosePrice,waPrice,close,volume,marketPrice2,
            marketPrice3,admittedQuote,mp2ValTrd,marketPrice3TradesValue,admittedValue,waVal,tradingSession,currencyId,trendClsPr
        );
    }
}
