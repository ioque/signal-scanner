package ru.ioque.acceptance.tradingDataGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.acceptance.application.tradingdatagenerator.TradingDataGeneratorFacade;
import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.acceptance.application.tradingdatagenerator.currencypair.CurrencyPairTradeGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.futures.FuturesTradesGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.index.IndexDeltasGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockTradesGeneratorConfig;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairDailyResult;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairTrade;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesDailyResult;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesTrade;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDailyResult;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDelta;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TradingDataGeneratorFacadeTest {
    TradingDataGeneratorFacade generator = new TradingDataGeneratorFacade();
    @Test
    @DisplayName("""
        T1. Генерация исторических данных для акций
        """)
    void testCase1() {
        List<StockDailyResult> stockDailyResults = generator.generateStockHistory(
            HistoryGeneratorConfig
                .builder()
                .ticker("SBER")
                .startClose(10.)
                .startOpen(10.)
                .startValue(1000D)
                .days(180)
                .startDate(LocalDate.parse("2022-01-10"))
                .openPricePercentageGrowths(List.of(new PercentageGrowths(10D, 0.5), new PercentageGrowths(-10D, 0.5)))
                .closePricePercentageGrowths(List.of(new PercentageGrowths(15D, 0.5), new PercentageGrowths(-5D, 0.5)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(15D, 0.5), new PercentageGrowths(-5D, 0.5)))
                .build()
        );

        assertEquals(129, stockDailyResults.size());
        assertEquals(1091, Math.round((Double) stockDailyResults.get(128).getValue().getValue()));
        assertEquals(11, Math.round((Double) stockDailyResults.get(128).getClose().getValue()));
    }

    @Test
    @DisplayName("""
        T2. Генерация внутридневных данных для акций
        """)
    void testCase2() {
        List<StockTrade> stockTrades = generator.generateStockTrades(
            StockTradesGeneratorConfig
                .builder()
                .ticker("SBER")
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

    @Test
    @DisplayName("""
        T3. Генерация исторических данных для индексов
        """)
    void testCase3() {
        List<IndexDailyResult> indexDailyResults = generator.generateIndexHistory(
            HistoryGeneratorConfig
                .builder()
                .ticker("IMOEX")
                .startClose(10.)
                .startOpen(10.)
                .startValue(1000D)
                .days(180)
                .startDate(LocalDate.parse("2022-01-10"))
                .openPricePercentageGrowths(List.of(new PercentageGrowths(10D, 0.5), new PercentageGrowths(-10D, 0.5)))
                .closePricePercentageGrowths(List.of(new PercentageGrowths(15D, 0.5), new PercentageGrowths(-5D, 0.5)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(15D, 0.5), new PercentageGrowths(-5D, 0.5)))
                .build()
        );
        assertFalse(indexDailyResults.isEmpty());
    }

    @Test
    @DisplayName("""
        T4. Генерация внутридневных данных для индексов
        """)
    void testCase4() {
        List<IndexDelta> indexDeltas = generator.generateIndexDeltas(
            IndexDeltasGeneratorConfig.builder().build()
        );
        assertFalse(indexDeltas.isEmpty());
    }

    @Test
    @DisplayName("""
        T5. Генерация исторических данных для валютных пар
        """)
    void testCase5() {
        List<CurrencyPairDailyResult> currencyPairDailyResults = generator.generateCurrencyPairHistory(
            HistoryGeneratorConfig
                .builder()
                .ticker("USDRUB")
                .startClose(10.)
                .startOpen(10.)
                .startValue(1000D)
                .days(180)
                .startDate(LocalDate.parse("2022-01-10"))
                .openPricePercentageGrowths(List.of(new PercentageGrowths(10D, 0.5), new PercentageGrowths(-10D, 0.5)))
                .closePricePercentageGrowths(List.of(new PercentageGrowths(15D, 0.5), new PercentageGrowths(-5D, 0.5)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(15D, 0.5), new PercentageGrowths(-5D, 0.5)))
                .build()
        );
        assertFalse(currencyPairDailyResults.isEmpty());
    }

    @Test
    @DisplayName("""
        T6. Генерация внутридневных данных для валютных пар
        """)
    void testCase6() {
        List<CurrencyPairTrade> currencyPairTrades = generator.generateCurrencyPairTrades(
            CurrencyPairTradeGeneratorConfig.builder().build()
        );
        assertFalse(currencyPairTrades.isEmpty());
    }

    @Test
    @DisplayName("""
        T7. Генерация исторических данных для фьючерсов
        """)
    void testCase7() {
        List<FuturesDailyResult> futuresDailyResults = generator.generateFuturesHistory(
            HistoryGeneratorConfig
                .builder()
                .ticker("BRF4")
                .startClose(10.)
                .startOpen(10.)
                .startValue(1000D)
                .days(180)
                .startDate(LocalDate.parse("2022-01-10"))
                .openPricePercentageGrowths(List.of(new PercentageGrowths(10D, 0.5), new PercentageGrowths(-10D, 0.5)))
                .closePricePercentageGrowths(List.of(new PercentageGrowths(15D, 0.5), new PercentageGrowths(-5D, 0.5)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(15D, 0.5), new PercentageGrowths(-5D, 0.5)))
                .build()
        );
        assertFalse(futuresDailyResults.isEmpty());
    }

    @Test
    @DisplayName("""
        T8. Генерация внутридневных данных для фьючерсов
        """)
    void testCase8() {
        List<FuturesTrade> futuresTrades = generator.generateFuturesTrades(
            FuturesTradesGeneratorConfig.builder().build()
        );
        assertFalse(futuresTrades.isEmpty());
    }
}
