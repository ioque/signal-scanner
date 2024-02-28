package ru.ioque.acceptance.domain.dataemulator.currencyPair;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.domain.dataemulator.core.DatasetValue;
import ru.ioque.acceptance.domain.dataemulator.core.DoubleValue;
import ru.ioque.acceptance.domain.dataemulator.core.DailyResultValue;
import ru.ioque.acceptance.domain.dataemulator.core.LocalDateValue;
import ru.ioque.acceptance.domain.dataemulator.core.StringValue;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyPairDailyResult extends DailyResultValue {
    StringValue boardId;
    StringValue shortName;
    DoubleValue open;
    DoubleValue low;
    DoubleValue high;
    DoubleValue close;
    DoubleValue numTrades;
    DoubleValue volRur;
    DoubleValue waPrice;

    @Builder
    public CurrencyPairDailyResult(
        String boardId,
        LocalDate tradeDate,
        String shortName,
        String secId,
        Double open,
        Double low,
        Double high,
        Double close,
        Double numTrades,
        Double volRur,
        Double waPrice
    ) {
        super(new StringValue("SECID", 4, secId), new LocalDateValue("TRADEDATE", 2, tradeDate));
        this.boardId = new StringValue("BOARDID", 1, boardId);
        this.shortName = new StringValue("SHORTNAME", 3, shortName);
        this.open = new DoubleValue("OPEN", 5, open);
        this.low = new DoubleValue("LOW", 6, low);
        this.high = new DoubleValue("HIGH", 7, high);
        this.close = new DoubleValue("CLOSE", 8, close);
        this.numTrades = new DoubleValue("NUMTRADES", 9, numTrades);
        this.volRur = new DoubleValue("VOLRUR", 10, volRur);
        this.waPrice = new DoubleValue("WAPRICE", 11, waPrice);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            boardId,tradeDate,shortName,secId,open,low,high,close,numTrades,volRur,waPrice
        );
    }
}
