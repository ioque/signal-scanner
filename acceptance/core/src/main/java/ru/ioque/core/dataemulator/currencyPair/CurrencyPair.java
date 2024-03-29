package ru.ioque.core.dataemulator.currencyPair;

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
import ru.ioque.core.dataemulator.core.LocalDateValue;
import ru.ioque.core.dataemulator.core.StringValue;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyPair extends InstrumentValue {
    StringValue boardId;
    StringValue shortname;
    IntegerValue lotSize;
    LocalDateValue settleDate;
    IntegerValue decimals;
    DoubleValue faceValue;
    StringValue marketCode;
    DoubleValue minStep;
    LocalDateValue prevDate;
    StringValue secName;
    StringValue remarks;
    StringValue status;
    StringValue faceUnit;
    DoubleValue prevPrice;
    DoubleValue prevWaPrice;
    StringValue currencyId;
    StringValue latName;
    IntegerValue lotDivider;

    @Builder
    public CurrencyPair(
        InstrumentType type,
        String secId,
        String boardId,
        String shortname,
        Integer lotSize,
        LocalDate settleDate,
        Integer decimals,
        Double faceValue,
        String marketCode,
        Double minStep,
        LocalDate prevDate,
        String secName,
        String remarks,
        String status,
        String faceUnit,
        Double prevPrice,
        Double prevWaPrice,
        String currencyId,
        String latName,
        Integer lotDivider
    ) {
        super(type, new StringValue("SECID", 1, secId));
        this.boardId = new StringValue("BOARDID", 2, boardId);
        this.shortname = new StringValue("SHORTNAME", 3, shortname);
        this.lotSize = new IntegerValue("LOTSIZE", 4, lotSize);
        this.settleDate = new LocalDateValue("SETTLEDATE", 5, settleDate);
        this.decimals = new IntegerValue("DECIMALS", 6, decimals);
        this.faceValue = new DoubleValue("FACEVALUE", 7, faceValue);
        this.marketCode = new StringValue("MARKETCODE", 8, marketCode);
        this.minStep = new DoubleValue("MINSTEP", 9, minStep);
        this.prevDate = new LocalDateValue("PREVDATE", 10, prevDate);
        this.secName = new StringValue("SECNAME", 11, secName);
        this.remarks = new StringValue("REMARKS", 12, remarks);
        this.status = new StringValue("STATUS", 13, status);
        this.faceUnit = new StringValue("FACEUNIT", 14, faceUnit);
        this.prevPrice = new DoubleValue("PREVPRICE", 15, prevPrice);
        this.prevWaPrice = new DoubleValue("PREVWAPRICE", 16, prevWaPrice);
        this.currencyId = new StringValue("CURRENCYID", 17, currencyId);
        this.latName = new StringValue("LATNAME", 18, latName);
        this.lotDivider = new IntegerValue("LOTDIVIDER", 19, lotDivider);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            secId,boardId,shortname,lotSize,settleDate,decimals,faceValue,marketCode,minStep,prevDate,
            secName,remarks,status,faceUnit,prevPrice,prevWaPrice,currencyId,latName,lotDivider
        );
    }
}
