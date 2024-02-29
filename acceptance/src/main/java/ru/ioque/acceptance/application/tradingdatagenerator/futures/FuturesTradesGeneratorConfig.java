package ru.ioque.acceptance.application.tradingdatagenerator.futures;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.application.tradingdatagenerator.core.IntradayGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FuturesTradesGeneratorConfig extends IntradayGeneratorConfig {
    @Builder
    public FuturesTradesGeneratorConfig(
        String ticker,
        LocalDate date,
        LocalTime startTime,
        long numTrades,
        double startPrice,
        List<PercentageGrowths> pricePercentageGrowths
    ) {
        super(ticker, date, startTime, numTrades, startPrice, pricePercentageGrowths);
    }
}
