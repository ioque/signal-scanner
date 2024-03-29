package ru.ioque.core.dataemulator.index;

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
public class IndexDailyResult extends DailyResultValue {
    StringValue shortName;
    StringValue name;
    DoubleValue value;
    IntegerValue duration;
    DoubleValue yield;
    IntegerValue decimals;
    DoubleValue capitalization;
    StringValue currencyId;
    DoubleValue divisor;
    StringValue tradingSession;
    DoubleValue volume;

    @Builder
    public IndexDailyResult(
        String boardId,
        String secId,
        LocalDate tradeDate,
        String shortName,
        String name,
        Double close,
        Double open,
        Double high,
        Double low,
        Double value,
        Integer duration,
        Double yield,
        Integer decimals,
        Double capitalization,
        String currencyId,
        Double divisor,
        String tradingSession,
        Double volume
    ) {
        super(
            new StringValue("SECID", 2, secId),
            new LocalDateValue("TRADEDATE", 3, tradeDate),
            new StringValue("BOARDID", 1, boardId),
            new DoubleValue("OPEN", 7, open),
            new DoubleValue("LOW", 9, low),
            new DoubleValue("HIGH", 8, high),
            new DoubleValue("CLOSE", 6, close)
        );
        this.shortName = new StringValue("SHORTNAME", 4, shortName);
        this.name = new StringValue("NAME", 5, name);
        this.value = new DoubleValue("VALUE", 10, value);
        this.duration = new IntegerValue("DURATION", 11, duration);
        this.yield = new DoubleValue("YIELD", 12, yield);
        this.decimals = new IntegerValue("DECIMALS", 13, decimals);
        this.capitalization = new DoubleValue("CAPITALIZATION", 14, capitalization);
        this.currencyId = new StringValue("CURRENCYID", 15, currencyId);
        this.divisor = new DoubleValue("DIVISOR", 16, divisor);
        this.tradingSession = new StringValue("TRADINGSESSION", 17, tradingSession);
        this.volume = new DoubleValue("VOLUME", 18, volume);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            boardId,secId,tradeDate,shortName,name,close,open,high,low,value,duration,yield,decimals,
            capitalization,currencyId,divisor,tradingSession,volume
        );
    }
}