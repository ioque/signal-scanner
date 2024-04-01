package ru.ioque.core.datagenerator.config;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.core.datagenerator.core.IntradayGeneratorConfig;
import ru.ioque.core.datagenerator.core.ParameterConfig;
import ru.ioque.core.datagenerator.core.PercentageGrowths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeltasGeneratorConfig extends IntradayGeneratorConfig {
    ParameterConfig value;

    @Builder
    public DeltasGeneratorConfig(
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
