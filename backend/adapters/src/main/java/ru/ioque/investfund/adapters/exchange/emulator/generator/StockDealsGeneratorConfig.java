package ru.ioque.investfund.adapters.exchange.emulator.generator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockDealsGeneratorConfig extends GeneratorConfig {
    LocalDate date;
    LocalTime startTime;
    long numTrades;
    double startPrice;
    double startValue;
    List<PercentageGrowths> pricePercentageGrowths;
    List<PercentageGrowths> valuePercentageGrowths;

    @Builder
    public StockDealsGeneratorConfig(
        String ticker,
        LocalDate date,
        LocalTime startTime,
        long numTrades,
        double startPrice,
        double startValue,
        List<PercentageGrowths> pricePercentageGrowths,
        List<PercentageGrowths> valuePercentageGrowths
    ) {
        super(ticker);
        this.date = date;
        this.startTime = startTime;
        this.numTrades = numTrades;
        this.startPrice = startPrice;
        this.startValue = startValue;
        this.pricePercentageGrowths = pricePercentageGrowths;
        this.valuePercentageGrowths = valuePercentageGrowths;
    }
}
