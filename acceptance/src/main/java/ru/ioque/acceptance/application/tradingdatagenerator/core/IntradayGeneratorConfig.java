package ru.ioque.acceptance.application.tradingdatagenerator.core;

import lombok.AccessLevel;
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
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class IntradayGeneratorConfig extends GeneratorConfig {
    LocalDate date;
    LocalTime startTime;
    long numTrades;
    ParameterConfig price;
    public IntradayGeneratorConfig(
        String ticker,
        LocalDate date,
        LocalTime startTime,
        long numTrades,
        double startPrice,
        List<PercentageGrowths> pricePercentageGrowths
    ) {
        super(ticker);
        this.date = date;
        this.startTime = startTime;
        this.numTrades = numTrades;
        this.price = new ParameterConfig("price", startPrice, pricePercentageGrowths);
    }
}
