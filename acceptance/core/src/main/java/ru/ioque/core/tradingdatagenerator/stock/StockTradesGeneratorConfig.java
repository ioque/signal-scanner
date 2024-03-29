package ru.ioque.core.tradingdatagenerator.stock;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.core.tradingdatagenerator.core.IntradayGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.core.ParameterConfig;
import ru.ioque.core.tradingdatagenerator.core.PercentageGrowths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockTradesGeneratorConfig extends IntradayGeneratorConfig {
    ParameterConfig value;
    @Builder
    public StockTradesGeneratorConfig(
        String ticker,
        LocalDate date,
        LocalTime startTime,
        long numTrades,
        double startPrice,
        double startValue,
        List<PercentageGrowths> pricePercentageGrowths,
        List<PercentageGrowths> valuePercentageGrowths
    ) {
        super(ticker, date, startTime, numTrades, startPrice, pricePercentageGrowths);
        this.value = new ParameterConfig("value", startValue, valuePercentageGrowths);
    }
}
