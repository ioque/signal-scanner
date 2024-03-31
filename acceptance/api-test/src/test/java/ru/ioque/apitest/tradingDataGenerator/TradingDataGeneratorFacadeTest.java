package ru.ioque.apitest.tradingDataGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.core.model.history.HistoryValue;
import ru.ioque.core.model.intraday.Contract;
import ru.ioque.core.model.intraday.Deal;
import ru.ioque.core.model.intraday.Delta;
import ru.ioque.core.tradingdatagenerator.TradingDataGeneratorFacade;
import ru.ioque.core.tradingdatagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.core.tradingdatagenerator.futures.FuturesTradesGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.index.IndexDeltasGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.stock.DealGeneratorConfig;

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
        List<HistoryValue> stockDailyResults = generator.generateHistory(
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
        assertEquals(1091, Math.round(stockDailyResults.get(128).getValue()));
        assertEquals(11, Math.round(stockDailyResults.get(128).getClosePrice()));
    }

    @Test
    @DisplayName("""
        T2. Генерация внутридневных данных для акций
        """)
    void testCase2() {
        List<Deal> stockTrades = generator.generateDeals(
            DealGeneratorConfig
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
        assertEquals(12.0, stockTrades.get(9).getPrice());
        assertEquals(120.0, stockTrades.get(9).getValue());
        assertEquals(10.92, stockTrades.get(19).getPrice());
        assertEquals(72.0, stockTrades.get(19).getValue());
    }

    @Test
    @DisplayName("""
        T3. Генерация исторических данных для индексов
        """)
    void testCase3() {
        List<HistoryValue> indexDailyResults = generator.generateHistory(
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
        List<Delta> indexDeltas = generator.generateDeltas(
            IndexDeltasGeneratorConfig.builder()
                .ticker("IMOEX")
                .numTrades(20)
                .startPrice(10.)
                .startValue(100D)
                .date(LocalDate.parse("2022-01-10"))
                .startTime(LocalTime.parse("10:00"))
                .pricePercentageGrowths(List.of(new PercentageGrowths(20D, 0.5), new PercentageGrowths(-9D, 0.5)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(20D, 0.5), new PercentageGrowths(-40D, 0.5)))
                .build()
        );
        assertFalse(indexDeltas.isEmpty());
    }

    @Test
    @DisplayName("""
        T5. Генерация исторических данных для валютных пар
        """)
    void testCase5() {
        List<HistoryValue> currencyPairDailyResults = generator.generateHistory(
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
        List<Deal> currencyPairTrades = generator.generateDeals(
            DealGeneratorConfig.builder()
                .ticker("USDRUB")
                .numTrades(20)
                .startPrice(10.)
                .startValue(100D)
                .date(LocalDate.parse("2022-01-10"))
                .startTime(LocalTime.parse("10:00"))
                .pricePercentageGrowths(List.of(new PercentageGrowths(20D, 0.5), new PercentageGrowths(-9D, 0.5)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(20D, 0.5), new PercentageGrowths(-40D, 0.5)))
                .build()
        );
        assertFalse(currencyPairTrades.isEmpty());
    }

    @Test
    @DisplayName("""
        T7. Генерация исторических данных для фьючерсов
        """)
    void testCase7() {
        List<HistoryValue> futuresDailyResults = generator.generateHistory(
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
        List<Contract> futuresTrades = generator.generateContracts(
            FuturesTradesGeneratorConfig.builder()
                .ticker("BRF4")
                .numTrades(20)
                .startPrice(10.)
                .date(LocalDate.parse("2022-01-10"))
                .startTime(LocalTime.parse("10:00"))
                .pricePercentageGrowths(List.of(new PercentageGrowths(20D, 0.5), new PercentageGrowths(-9D, 0.5)))
                .build()
        );
        assertFalse(futuresTrades.isEmpty());
    }
}
