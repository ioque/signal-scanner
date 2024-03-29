package ru.ioque.core.dataemulator.index;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.core.dataemulator.core.DatasetValue;
import ru.ioque.core.dataemulator.core.DoubleValue;
import ru.ioque.core.dataemulator.core.InstrumentType;
import ru.ioque.core.dataemulator.core.InstrumentValue;
import ru.ioque.core.dataemulator.core.IntegerValue;
import ru.ioque.core.dataemulator.core.StringValue;

import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Index extends InstrumentValue {
    StringValue boardId;
    StringValue name;
    IntegerValue decimals;
    StringValue shortName;
    DoubleValue annualHigh;
    DoubleValue annualLow;
    StringValue currencyId;
    StringValue calcMode;

    @Builder
    public Index(
        InstrumentType type,
        String secId,
        String boardId,
        String name,
        Integer decimals,
        String shortName,
        Double annualHigh,
        Double annualLow,
        String currencyId,
        String calcMode
    ) {
        super(type, new StringValue("SECID", 1, secId));
        this.boardId = new StringValue("BOARDID", 2, boardId);
        this.name = new StringValue("NAME", 3, name);
        this.decimals = new IntegerValue("DECIMALS", 4, decimals);
        this.shortName = new StringValue("SHORTNAME", 5, shortName);
        this.annualHigh = new DoubleValue("ANNUALHIGH", 6, annualHigh);
        this.annualLow = new DoubleValue("ANNUALLOW", 7, annualLow);
        this.currencyId = new StringValue("CURRENCYID", 8, currencyId);
        this.calcMode = new StringValue("CALCMODE", 9, calcMode);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            secId,
            boardId,
            name,
            decimals,
            shortName,
            annualHigh,
            annualLow,
            currencyId,
            calcMode
        );
    }
}
