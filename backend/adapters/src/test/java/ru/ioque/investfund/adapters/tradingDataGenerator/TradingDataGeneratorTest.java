package ru.ioque.investfund.adapters.tradingDataGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.adapters.exchange.emulator.generator.PercentageGrowths;
import ru.ioque.investfund.adapters.exchange.emulator.generator.StockDealResultsGeneratorConfig;
import ru.ioque.investfund.adapters.exchange.emulator.generator.StockDealsGeneratorConfig;
import ru.ioque.investfund.adapters.exchange.emulator.generator.TradingDataGenerator;
import ru.ioque.investfund.domain.exchange.value.tradingData.Deal;
import ru.ioque.investfund.domain.exchange.value.tradingData.DealResult;

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
        List<DealResult> stockDailyResults = generator.generateStockDealResults(
            StockDealResultsGeneratorConfig
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
        assertEquals(1050D, stockDailyResults.get(179).getValue());
        assertEquals(10.5, stockDailyResults.get(179).getClosePrice());
    }

    @Test
    @DisplayName("""
        T2. Генерация внутридневных данных для акций
        """)
    void testCase2() {
        List<Deal> stockTrades = generator.generateStockDeals(
            StockDealsGeneratorConfig
                .builder()
                .numTrades(10)
                .startPrice(10.)
                .startValue(100D)
                .date(LocalDate.parse("2022-01-10"))
                .startTime(LocalTime.parse("10:00"))
                .pricePercentageGrowths(List.of(new PercentageGrowths(9, 1)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(9, 1)))
                .build()
        );
        assertEquals(10, stockTrades.size());
        assertEquals(11.0, stockTrades.get(9).getPrice());
        assertEquals(110.0, stockTrades.get(9).getValue());
    }
}
