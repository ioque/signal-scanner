package ru.ioque.acceptance.application.tradingdatagenerator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockHistoryGeneratorConfig extends GeneratorConfig {
    double startValue;
    double startOpen;
    double startClose;
    List<PercentageGrowths> openPricePercentageGrowths;
    List<PercentageGrowths> closePricePercentageGrowths;
    List<PercentageGrowths> valuePercentageGrowths;

    @Builder
    public StockHistoryGeneratorConfig(
        String ticker,
        LocalDate startDate,
        int days,
        double startValue,
        double startOpen,
        double startClose,
        List<PercentageGrowths> openPricePercentageGrowths,
        List<PercentageGrowths> closePricePercentageGrowths,
        List<PercentageGrowths> valuePercentageGrowths
    ) {
        super(ticker, startDate, days);
        this.startValue = startValue;
        this.startOpen = startOpen;
        this.startClose = startClose;
        this.startDate = startDate;
        this.days = days;
        this.openPricePercentageGrowths = openPricePercentageGrowths;
        this.closePricePercentageGrowths = closePricePercentageGrowths;
        this.valuePercentageGrowths = valuePercentageGrowths;
    }
}


