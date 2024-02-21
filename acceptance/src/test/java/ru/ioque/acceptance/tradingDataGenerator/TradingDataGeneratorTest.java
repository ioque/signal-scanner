package ru.ioque.acceptance.tradingDataGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.acceptance.application.tradingdatagenerator.PercentageGrowths;
import ru.ioque.acceptance.application.tradingdatagenerator.StockHistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.TradingDataGenerator;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradingDataGeneratorTest {
    TradingDataGenerator generator = new TradingDataGenerator();
    @Test
    @DisplayName("""
        T1. Генерация исторических данных для акций
        """)
    void testCase1() {
        List<StockDailyResult> stockDailyResults = generator.generateStockHistory(
            StockHistoryGeneratorConfig
                .builder()
                .startClose(10.)
                .startOpen(10.)
                .startValue(1000D)
                .days(180)
                .startDate(LocalDate.parse("2022-01-10"))
                .openPricePercentageGrowths(List.of(new PercentageGrowths(5, 1)))
                .closePricePercentageGrowths(List.of(new PercentageGrowths(5, 1)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(5, 1)))
                .build()
        );

        assertEquals(180, stockDailyResults.size());
        assertEquals(1050D, stockDailyResults.get(179).getValue().getValue());
        assertEquals(10.5, stockDailyResults.get(179).getClose().getValue());
    }
}
