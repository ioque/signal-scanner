package ru.ioque.core.datagenerator.core;

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
public class HistoryGeneratorConfig extends GeneratorConfig {
    LocalDate startDate;
    int days;
    ParameterConfig tradeValue;
    ParameterConfig openPrice;
    ParameterConfig closePrice;

    @Builder
    public HistoryGeneratorConfig(
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
        this.tradeValue = new ParameterConfig("tradeValue", startValue, valuePercentageGrowths);
        this.openPrice = new ParameterConfig("openPrice", startOpen, openPricePercentageGrowths);
        this.closePrice = new ParameterConfig("startClose", startClose, closePricePercentageGrowths);
    }
}
