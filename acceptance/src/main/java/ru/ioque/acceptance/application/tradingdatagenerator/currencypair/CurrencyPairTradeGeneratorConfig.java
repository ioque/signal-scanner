package ru.ioque.acceptance.application.tradingdatagenerator.currencypair;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.application.tradingdatagenerator.core.IntradayGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.core.ParameterConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyPairTradeGeneratorConfig extends IntradayGeneratorConfig {
    ParameterConfig value;
    @Builder
    public CurrencyPairTradeGeneratorConfig(
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
