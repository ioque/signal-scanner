package ru.ioque.acceptance.domain.dataemulator.futures;

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
public class FuturesDailyResult extends DailyResultValue {
    DoubleValue openPositionValue;
    DoubleValue value;
    IntegerValue volume;
    IntegerValue openPosition;
    DoubleValue settlePrice;
    DoubleValue swapRate;

    @Builder
    public FuturesDailyResult(
        String boardId,
        LocalDate tradeDate,
        String secId,
        Double open,
        Double low,
        Double high,
        Double close,
        Double openPositionValue,
        Double value,
        Integer volume,
        Integer openPosition,
        Double settlePrice,
        Double swapRate
    ) {
        super(new StringValue("SECID", 3, secId),
            new LocalDateValue("TRADEDATE", 2, tradeDate),
            new StringValue("BOARDID", 1, boardId),
            new DoubleValue("OPEN", 4, open),
            new DoubleValue("LOW", 5, low),
            new DoubleValue("HIGH", 6, high),
            new DoubleValue("CLOSE", 7, close)
        );
        this.openPositionValue = new DoubleValue("OPENPOSITIONVALUE", 8, openPositionValue);
        this.value = new DoubleValue("VALUE", 9, value);
        this.volume = new IntegerValue("VOLUME", 10, volume);
        this.openPosition = new IntegerValue("OPENPOSITION", 11, openPosition);
        this.settlePrice = new DoubleValue("SETTLEPRICE", 12, settlePrice);
        this.swapRate = new DoubleValue("SWAPRATE", 13, swapRate);
    }

    public List<? extends DatasetValue> getRow() {
        return List.of(
            boardId,tradeDate,secId,open,low,high,close,openPositionValue,value,volume,openPosition,settlePrice,swapRate
        );
    }
}
