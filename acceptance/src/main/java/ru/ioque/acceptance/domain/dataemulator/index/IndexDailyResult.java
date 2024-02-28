package ru.ioque.acceptance.domain.dataemulator.index;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.domain.dataemulator.core.DatasetValue;
import ru.ioque.acceptance.domain.dataemulator.core.DoubleValue;
import ru.ioque.acceptance.domain.dataemulator.core.DailyResultValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntegerValue;
import ru.ioque.acceptance.domain.dataemulator.core.LocalDateValue;
import ru.ioque.acceptance.domain.dataemulator.core.StringValue;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IndexDailyResult extends DailyResultValue {
    StringValue boardId;
    StringValue shortName;
    StringValue name;
    DoubleValue close;
    DoubleValue open;
    DoubleValue high;
    DoubleValue low;
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
        super(new StringValue("SECID", 2, secId), new LocalDateValue("TRADEDATE", 3, tradeDate));
        this.boardId = new StringValue("BOARDID", 1, boardId);
        this.secId = new StringValue("SECID", 2, secId);
        this.shortName = new StringValue("SHORTNAME", 4, shortName);
        this.name = new StringValue("NAME", 5, name);
        this.close = new DoubleValue("CLOSE", 6, close);
        this.open = new DoubleValue("OPEN", 7, open);
        this.high = new DoubleValue("HIGH", 8, high);
        this.low = new DoubleValue("LOW", 9, low);
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