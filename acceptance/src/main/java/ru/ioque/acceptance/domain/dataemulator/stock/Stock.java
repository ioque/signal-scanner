package ru.ioque.acceptance.domain.dataemulator.stock;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.domain.dataemulator.core.DatasetValue;
import ru.ioque.acceptance.domain.dataemulator.core.DoubleValue;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentType;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntegerValue;
import ru.ioque.acceptance.domain.dataemulator.core.LocalDateValue;
import ru.ioque.acceptance.domain.dataemulator.core.StringValue;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Stock extends InstrumentValue {
    StringValue boardId;
    StringValue shortname;
    DoubleValue prevPrice;
    IntegerValue lotSize;
    DoubleValue faceValue;
    StringValue status;
    StringValue boardName;
    IntegerValue decimals;
    StringValue secName;
    StringValue marketCode;
    StringValue instrId;
    DoubleValue minStep;
    DoubleValue prevWaPrice;
    DoubleValue faceUnit;
    LocalDateValue prevDate;
    DoubleValue issueSize;
    StringValue isin;
    StringValue latName;
    StringValue regNumber;
    DoubleValue prevLegalClosePrice;
    StringValue currencyId;
    StringValue secType;
    IntegerValue listLevel;
    LocalDateValue settleDate;

    @Builder
    public Stock(
        InstrumentType type,
        String secId,
        String boardId,
        String shortname,
        Double prevPrice,
        Integer lotSize,
        Double faceValue,
        String status,
        String boardName,
        Integer decimals,
        String secName,
        String marketCode,
        String instrId,
        Double minStep,
        Double prevWaPrice,
        Double faceUnit,
        LocalDate prevDate,
        Double issueSize,
        String isin,
        String latName,
        String regNumber,
        Double prevLegalClosePrice,
        String currencyId,
        String secType,
        Integer listLevel,
        LocalDate settleDate
    ) {
        super(type, new StringValue("SECID", 1, secId));
        this.boardId = new StringValue("BOARDID", 2, boardId);
        this.shortname = new StringValue("SHORTNAME", 3, shortname);
        this.prevPrice = new DoubleValue("PREVPRICE", 4, prevPrice);
        this.lotSize = new IntegerValue("LOTSIZE", 5, lotSize);
        this.faceValue = new DoubleValue("FACEVALUE", 6, faceValue);
        this.status = new StringValue("STATUS", 7, status);
        this.boardName = new StringValue("BOARDNAME", 8, boardName);
        this.decimals = new IntegerValue("DECIMALS", 9, decimals);
        this.secName = new StringValue("SECNAME", 10, secName);
        this.marketCode = new StringValue("MARKETCODE", 11, marketCode);
        this.instrId = new StringValue("INSTRID", 12, instrId);
        this.minStep = new DoubleValue("MINSTEP", 13, minStep);
        this.prevWaPrice = new DoubleValue("PREVWAPRICE", 14, prevWaPrice);
        this.faceUnit = new DoubleValue("FACEUNIT", 15, faceUnit);
        this.prevDate = new LocalDateValue("PREVDATE", 16, prevDate);
        this.issueSize = new DoubleValue("ISSUESIZE", 17, issueSize);
        this.isin = new StringValue("ISIN", 18, isin);
        this.latName = new StringValue("LATNAME", 19, latName);
        this.regNumber = new StringValue("REGNUMBER", 20, regNumber);
        this.prevLegalClosePrice = new DoubleValue("PREVLEGALCLOSEPRICE", 21, prevLegalClosePrice);
        this.currencyId = new StringValue("CURRENCYID", 22, currencyId);
        this.secType = new StringValue("SECTYPE", 23, secType);
        this.listLevel = new IntegerValue("LISTLEVEL", 24, listLevel);
        this.settleDate = new LocalDateValue("SETTLEDATE", 25, settleDate);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            secId,
            boardId,
            shortname,
            prevPrice,
            lotSize,
            faceValue,
            status,
            boardName,
            decimals,
            secName,
            marketCode,
            instrId,
            minStep,
            prevWaPrice,
            faceUnit,
            prevDate,
            issueSize,
            isin,
            latName,
            regNumber,
            prevLegalClosePrice,
            currencyId,
            secType,
            listLevel,
            settleDate
        );
    }
}
