package ru.ioque.acceptance.perfomance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.api.BaseApiAcceptanceTest;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockHistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockTradesGeneratorConfig;
import ru.ioque.acceptance.domain.exchange.Instrument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PERFORMANCE")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class PerformanceAcceptanceTest extends BaseApiAcceptanceTest {
    @Test
    @DisplayName("""
        T1. Загрузка 50к сделок по торгам внутри дня
        """)
    void testCase1() {
        LocalDateTime time = LocalDateTime.now();
        LocalDate startDate = time.toLocalDate().minusMonths(6);
        integrateInstruments(instruments().sber().build());
        datasetManager().initIntradayValue(
            generator().generateStockTrades(
                StockTradesGeneratorConfig
                    .builder()
                    .ticker("SBER")
                    .numTrades(50000)
                    .startPrice(10.)
                    .startValue(100D)
                    .date(time.toLocalDate())
                    .startTime(LocalTime.parse("10:00"))
                    .pricePercentageGrowths(List.of(new PercentageGrowths(29D, 1D)))
                    .valuePercentageGrowths(List.of(new PercentageGrowths(29D, 1D)))
                    .build()
            )
        );
        datasetManager().initDailyResultValue(
            generator().generateStockHistory(
                StockHistoryGeneratorConfig
                    .builder()
                    .ticker("SBER")
                    .startClose(10.)
                    .startOpen(10.)
                    .startValue(1000D)
                    .days(180)
                    .startDate(startDate)
                    .openPricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                    .closePricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                    .valuePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                    .build()
            )
        );
        enableUpdateInstrumentBy(getInstrumentIds());
        long startMills = System.currentTimeMillis();
        integrateTradingData();
        long finishMills = System.currentTimeMillis();
        long seconds = ((finishMills - startMills) / 1000);

        Instrument sber = getInstrumentById(getInstrumentIds().get(0));
        assertEquals(180, sber.getDailyValues().size());
        assertEquals(50000, sber.getIntradayValues().size());
        assertTrue(seconds < 30);
        System.out.println("seconds = " + seconds);
    }
}
