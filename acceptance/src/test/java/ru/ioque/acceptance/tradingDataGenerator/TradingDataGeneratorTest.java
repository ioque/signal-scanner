package ru.ioque.acceptance.tradingDataGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.acceptance.application.tradingdatagenerator.PercentageGrowths;
import ru.ioque.acceptance.application.tradingdatagenerator.StockHistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.StockTradesGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.TradingDataGenerator;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;

import java.time.LocalDate;
import java.time.LocalTime;
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
                .openPricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                .closePricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                .build()
        );

        assertEquals(180, stockDailyResults.size());
        assertEquals(1050D, stockDailyResults.get(179).getValue().getValue());
        assertEquals(10.5, stockDailyResults.get(179).getClose().getValue());
    }

    @Test
    @DisplayName("""
        T2. Генерация внутридневных данных для акций
        """)
    void testCase2() {
        List<StockTrade> stockTrades = generator.generateStockTrades(
            StockTradesGeneratorConfig
                .builder()
                .numTrades(20)
                .startPrice(10.)
                .startValue(100D)
                .date(LocalDate.parse("2022-01-10"))
                .startTime(LocalTime.parse("10:00"))
                .pricePercentageGrowths(List.of(new PercentageGrowths(20D, 0.5), new PercentageGrowths(-9D, 0.5)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(20D, 0.5), new PercentageGrowths(-40D, 0.5)))
                .build()
        );
        assertEquals(20, stockTrades.size());
        assertEquals(12.0, stockTrades.get(9).getPrice().getValue());
        assertEquals(120.0, stockTrades.get(9).getValue().getValue());
        assertEquals(10.92, stockTrades.get(19).getPrice().getValue());
        assertEquals(72.0, stockTrades.get(19).getValue().getValue());
    }
}
