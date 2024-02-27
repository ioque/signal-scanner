package ru.ioque.investfund.adapters.exchange.emulator.generator;

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
public class StockDealResultsGeneratorConfig extends GeneratorConfig {
    LocalDate startDate;
    int days;
    double startValue;
    double startOpen;
    double startClose;
    List<PercentageGrowths> openPricePercentageGrowths;
    List<PercentageGrowths> closePricePercentageGrowths;
    List<PercentageGrowths> valuePercentageGrowths;

    @Builder
    public StockDealResultsGeneratorConfig(
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
        super(ticker);
        this.startDate = startDate;
        this.days = days;
        this.startValue = startValue;
        this.startOpen = startOpen;
        this.startClose = startClose;
        this.openPricePercentageGrowths = openPricePercentageGrowths;
        this.closePricePercentageGrowths = closePricePercentageGrowths;
        this.valuePercentageGrowths = valuePercentageGrowths;
    }
}


