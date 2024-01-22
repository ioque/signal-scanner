package ru.ioque.acceptance.domain.dataemulator.futures;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.domain.dataemulator.core.DatasetValue;
import ru.ioque.acceptance.domain.dataemulator.core.DoubleValue;
import ru.ioque.acceptance.domain.dataemulator.core.HistoryValue;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentType;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntegerValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntradayValue;
import ru.ioque.acceptance.domain.dataemulator.core.LocalDateTimeValue;
import ru.ioque.acceptance.domain.dataemulator.core.LocalDateValue;
import ru.ioque.acceptance.domain.dataemulator.core.StringValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Futures extends InstrumentValue {
    StringValue boardId;
    StringValue shortname;
    StringValue secName;
    DoubleValue prevSettlePrice;
    IntegerValue decimals;
    DoubleValue minStep;
    LocalDateValue lastTradeDate;
    LocalDateValue lastDelDate;
    StringValue secType;
    StringValue latName;
    StringValue assetCode;
    IntegerValue prevOpenPosition;
    IntegerValue lotVolume;
    DoubleValue initialMargin;
    DoubleValue highLimit;
    DoubleValue lowLimit;
    DoubleValue stepPrice;
    DoubleValue lastSettlePrice;
    DoubleValue prevPrice;
    LocalDateTimeValue imTime;
    DoubleValue buySellFee;
    DoubleValue scalPerFee;
    DoubleValue negotiatedFee;
    DoubleValue exerciseFee;

    @Builder
    public Futures(
        InstrumentType type,
        String secId,
        List<? extends IntradayValue> intradayValues,
        List<? extends HistoryValue> historyValues,
        String boardId,
        String shortname,
        String secName,
        Double prevSettlePrice,
        Integer decimals,
        Double minStep,
        LocalDate lastTradeDate,
        LocalDate lastDelDate,
        String secType,
        String latName,
        String assetCode,
        Integer prevOpenPosition,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        Double stepPrice,
        Double lastSettlePrice,
        Double prevPrice,
        LocalDateTime imTime,
        Double buySellFee,
        Double scalPerFee,
        Double negotiatedFee,
        Double exerciseFee
    ) {
        super(type, new StringValue("SECID", 1, secId), intradayValues, historyValues);
        this.boardId = new StringValue("BOARDID", 2, boardId);
        this.shortname = new StringValue("SHORTNAME", 3, shortname);
        this.secName = new StringValue("SECNAME", 4, secName);
        this.prevSettlePrice = new DoubleValue("PREVSETTLEPRICE", 5, prevSettlePrice);
        this.decimals = new IntegerValue("DECIMALS", 6, decimals);
        this.minStep = new DoubleValue("MINSTEP", 7, minStep);
        this.lastTradeDate = new LocalDateValue("LASTTRADEDATE", 8, lastTradeDate);
        this.lastDelDate = new LocalDateValue("LASTDELDATE", 9, lastDelDate);
        this.secType = new StringValue("SECTYPE", 10, secType);
        this.latName = new StringValue("LATNAME", 11, latName);
        this.assetCode = new StringValue("ASSETCODE", 12, assetCode);
        this.prevOpenPosition = new IntegerValue("PREVOPENPOSITION", 13, prevOpenPosition);
        this.lotVolume = new IntegerValue("LOTVOLUME", 14, lotVolume);
        this.initialMargin = new DoubleValue("INITIALMARGIN", 15, initialMargin);
        this.highLimit = new DoubleValue("HIGHLIMIT", 16, highLimit);
        this.lowLimit = new DoubleValue("LOWLIMIT", 17, lowLimit);
        this.stepPrice = new DoubleValue("STEPPRICE", 18, stepPrice);
        this.lastSettlePrice = new DoubleValue("LASTSETTLEPRICE", 19, lastSettlePrice);
        this.prevPrice = new DoubleValue("PREVPRICE", 20, prevPrice);
        this.imTime = new LocalDateTimeValue("IMTIME", 21, imTime);
        this.buySellFee = new DoubleValue("BUYSELLFEE", 22, buySellFee);
        this.scalPerFee = new DoubleValue("SCALPERFEE", 23, scalPerFee);
        this.negotiatedFee = new DoubleValue("NEGOTIATEDFEE", 24, negotiatedFee);
        this.exerciseFee = new DoubleValue("EXERCISEFEE", 25, exerciseFee);
    }

    @Override
    public List<? extends DatasetValue> getRow() {
        return List.of(
            secId,
            boardId,
            shortname,
            secName,
            prevSettlePrice,
            decimals,
            minStep,
            lastTradeDate,
            lastDelDate,
            secType,
            latName,
            assetCode,
            prevOpenPosition,
            lotVolume,
            initialMargin,
            highLimit,
            lowLimit,
            stepPrice,
            lastSettlePrice,
            prevPrice,
            imTime,
            buySellFee,
            scalPerFee,
            negotiatedFee,
            exerciseFee
        );
    }
}
