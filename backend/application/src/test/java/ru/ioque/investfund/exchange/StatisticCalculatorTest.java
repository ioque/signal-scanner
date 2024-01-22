package ru.ioque.investfund.exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EXCHANGE MANAGER - STATISTIC CALCULATION")
public class StatisticCalculatorTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        exchangeDataFixture().initInstruments(List.of(afks(), imoex(), usdRub(), brf4()));
        dateTimeProvider().setNow(LocalDateTime.parse("2023-12-08T10:15:00"));
        exchangeManager().integrateWithDataSource();
        clearLogs();
    }

    @Test
    @DisplayName("""
        T16. Хранилище финансовых инструментов не пустое.
        Сохранены торговые данные только по акции AFKS.
        Запрошена статистика по инструментам.
        Результат: статистика успешно расчитана только для акции AFKS.
        """)
    void testCase16() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDealBy(1L,"AFKS", "10:00:00", 10D, 10D, 1),
                buildDealBy(1L,"AFKS", "11:00:00", 10D, 10D, 1),
                buildDealBy(1L,"AFKS", "12:00:00", 10D, 10D, 1),
                buildDealBy(1L,"AFKS", "13:00:00", 10D, 10D, 1),
                buildDealBy(1L,"AFKS", "14:00:00", 10D, 10D, 1)
            )
        );
        exchangeDataFixture().initTradingResults(
            List.of(
                buildDealResultBy("AFKS", "2024-01-03", 10D, 10D, 10D, 10D),
                buildDealResultBy("AFKS", "2024-01-04", 10D, 10D, 10D, 10D),
                buildDealResultBy("AFKS", "2024-01-05", 10D, 10D, 10D, 10D),
                buildDealResultBy("AFKS", "2024-01-06", 10D, 10D, 10D, 10D)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().tradingDataIntegrate();

        List<InstrumentStatistic> instrumentStatistics = exchangeManager().getStatistics();

        assertEquals(1, instrumentStatistics.size());
        assertEquals(50D, instrumentStatistics.get(0).getTodayValue());
        assertEquals(10D, instrumentStatistics.get(0).getHistoryMedianValue());
        assertEquals(10D, instrumentStatistics.get(0).getTodayOpenPrice());
        assertEquals(10D, instrumentStatistics.get(0).getTodayLastPrice());
        assertEquals(4, instrumentStatistics.get(0).getClosePriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getOpenPriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getWaPriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getValueSeries().size());
        assertEquals(5, instrumentStatistics.get(0).getTodayPriceSeries().size());
    }

    @Test
    @DisplayName("""
        T17. Хранилище финансовых инструментов не пустое.
        Сохранены торговые данные только по фьючерсу BRF4.
        Запрошена статистика по инструментам.
        Результат: статистика успешно расчитана только для фьючерса BRF4. Значений по средневзвешенной цене нет.
        """)
    void testCase17() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildFuturesDealBy(1L,"BRF4", "10:00:00", 10D, 10),
                buildFuturesDealBy(1L,"BRF4", "11:00:00", 10D, 10),
                buildFuturesDealBy(1L,"BRF4", "12:00:00", 10D, 10),
                buildFuturesDealBy(1L,"BRF4", "13:00:00", 10D, 10),
                buildFuturesDealBy(1L,"BRF4", "14:00:00", 10D, 10)
            )
        );
        exchangeDataFixture().initTradingResults(
            List.of(
                buildFuturesDealResultBy("BRF4", "2024-01-03", 10D, 10D, 10D, 10),
                buildFuturesDealResultBy("BRF4", "2024-01-04", 10D, 10D, 10D, 10),
                buildFuturesDealResultBy("BRF4", "2024-01-05", 10D, 10D, 10D, 10),
                buildFuturesDealResultBy("BRF4", "2024-01-06", 10D, 10D, 10D, 10)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("BRF4")).map(Instrument::getId).toList());
        exchangeManager().tradingDataIntegrate();

        List<InstrumentStatistic> instrumentStatistics = exchangeManager().getStatistics();

        assertEquals(1, instrumentStatistics.size());
        assertEquals(500000D, instrumentStatistics.get(0).getTodayValue());
        assertEquals(10D, instrumentStatistics.get(0).getHistoryMedianValue());
        assertEquals(10D, instrumentStatistics.get(0).getTodayOpenPrice());
        assertEquals(10D, instrumentStatistics.get(0).getTodayLastPrice());
        assertEquals(4, instrumentStatistics.get(0).getClosePriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getOpenPriceSeries().size());
        assertEquals(0, instrumentStatistics.get(0).getWaPriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getValueSeries().size());
        assertEquals(5, instrumentStatistics.get(0).getTodayPriceSeries().size());
    }

    @Test
    @DisplayName("""
        T18. Хранилище финансовых инструментов не пустое.
        Сохранены торговые данные только по валютной паре USD000UTSTOM.
        Запрошена статистика по инструментам.
        Результат: статистика успешно расчитана только для валютной пары USD000UTSTOM.
        """)
    void testCase18() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDealBy(1L,"USD000UTSTOM", "10:00:00", 10D, 10D, 1),
                buildDealBy(1L,"USD000UTSTOM", "11:00:00", 10D, 10D, 1),
                buildDealBy(1L,"USD000UTSTOM", "12:00:00", 10D, 10D, 1),
                buildDealBy(1L,"USD000UTSTOM", "13:00:00", 10D, 10D, 1),
                buildDealBy(1L,"USD000UTSTOM", "14:00:00", 10D, 10D, 1)
            )
        );
        exchangeDataFixture().initTradingResults(
            List.of(
                buildDealResultBy("USD000UTSTOM", "2024-01-03", 10D, 10D, 10D, 10D),
                buildDealResultBy("USD000UTSTOM", "2024-01-04", 10D, 10D, 10D, 10D),
                buildDealResultBy("USD000UTSTOM", "2024-01-05", 10D, 10D, 10D, 10D),
                buildDealResultBy("USD000UTSTOM", "2024-01-06", 10D, 10D, 10D, 10D)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("USD000UTSTOM")).map(Instrument::getId).toList());
        exchangeManager().tradingDataIntegrate();

        List<InstrumentStatistic> instrumentStatistics = exchangeManager().getStatistics();

        assertEquals(1, instrumentStatistics.size());
        assertEquals(50D, instrumentStatistics.get(0).getTodayValue());
        assertEquals(10D, instrumentStatistics.get(0).getHistoryMedianValue());
        assertEquals(10D, instrumentStatistics.get(0).getTodayOpenPrice());
        assertEquals(10D, instrumentStatistics.get(0).getTodayLastPrice());
        assertEquals(4, instrumentStatistics.get(0).getClosePriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getOpenPriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getWaPriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getValueSeries().size());
        assertEquals(5, instrumentStatistics.get(0).getTodayPriceSeries().size());
    }

    @Test
    @DisplayName("""
        T19. Хранилище финансовых инструментов не пустое.
        Сохранены торговые данные только по индексу фондового рынка IMOEX.
        Запрошена статистика по инструментам.
        Результат: статистика успешно расчитана только для индекса фондового рынка IMOEX. Значений по средневзвешенной цене нет.
        """)
    void testCase19() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDeltaBy(1L,"IMOEX", "10:00:00", 10D, 10D),
                buildDeltaBy(1L,"IMOEX", "11:00:00", 10D, 10D),
                buildDeltaBy(1L,"IMOEX", "12:00:00", 10D, 10D),
                buildDeltaBy(1L,"IMOEX", "13:00:00", 10D, 10D),
                buildDeltaBy(1L,"IMOEX", "14:00:00", 10D, 10D)
            )
        );
        exchangeDataFixture().initTradingResults(
            List.of(
                buildDeltaResultBy("IMOEX", "2024-01-03", 10D, 10D, 10D),
                buildDeltaResultBy("IMOEX", "2024-01-04", 10D, 10D, 10D),
                buildDeltaResultBy("IMOEX", "2024-01-05", 10D, 10D, 10D),
                buildDeltaResultBy("IMOEX", "2024-01-06", 10D, 10D, 10D)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("IMOEX")).map(Instrument::getId).toList());
        exchangeManager().tradingDataIntegrate();

        List<InstrumentStatistic> instrumentStatistics = exchangeManager().getStatistics();

        assertEquals(1, instrumentStatistics.size());
        assertEquals(50D, instrumentStatistics.get(0).getTodayValue());
        assertEquals(10D, instrumentStatistics.get(0).getHistoryMedianValue());
        assertEquals(10D, instrumentStatistics.get(0).getTodayOpenPrice());
        assertEquals(10D, instrumentStatistics.get(0).getTodayLastPrice());
        assertEquals(4, instrumentStatistics.get(0).getClosePriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getOpenPriceSeries().size());
        assertEquals(0, instrumentStatistics.get(0).getWaPriceSeries().size());
        assertEquals(4, instrumentStatistics.get(0).getValueSeries().size());
        assertEquals(5, instrumentStatistics.get(0).getTodayPriceSeries().size());
    }

    @Test
    @DisplayName("""
        T20. Хранилище финансовых инструментов не пустое.
        Торговых данных нет.
        Запрошена статистика по инструментам.
        Результат: нет статистических данных.
        """)
    void testCase20() {
        assertEquals(0, exchangeManager().getStatistics().size());
    }

    @Test
    @DisplayName("""
        T21. Хранилище финансовых инструментов не пустое.
        Сохранена только история торгов по инструменту AFKS.
        Запрошена статистика по инструментам.
        Результат: нет статистических данных.
        """)
    void testCase21() {
        exchangeDataFixture().initTradingResults(
            List.of(
                buildDealResultBy("AFKS", "2024-01-03", 10D, 10D, 10D, 10D),
                buildDealResultBy("AFKS", "2024-01-04", 10D, 10D, 10D, 10D),
                buildDealResultBy("AFKS", "2024-01-05", 10D, 10D, 10D, 10D),
                buildDealResultBy("AFKS", "2024-01-06", 10D, 10D, 10D, 10D)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().tradingDataIntegrate();
        assertEquals(0, exchangeManager().getStatistics().size());
    }

    @Test
    @DisplayName("""
        T22. Хранилище финансовых инструментов не пустое.
        Сохранены только данные по текущим торгам.
        Запрошена статистика по инструментам.
        Результат: нет статистических данных.
        """)
    void testCase22() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDealBy(1L,"AFKS", "10:00:00", 10D, 10D, 1),
                buildDealBy(1L,"AFKS", "11:00:00", 10D, 10D, 1),
                buildDealBy(1L,"AFKS", "12:00:00", 10D, 10D, 1),
                buildDealBy(1L,"AFKS", "13:00:00", 10D, 10D, 1),
                buildDealBy(1L,"AFKS", "14:00:00", 10D, 10D, 1)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().tradingDataIntegrate();
        assertEquals(0, exchangeManager().getStatistics().size());
    }

    @Test
    @DisplayName("""
        T23. Хранилище финансовых инструментов не пустое.
        Текущий день недели - понедельник. Данные по сделкам есть.
        Исторические данные загружены.
        Результат: статистика посчитана. Рост за предыдущий день определяется.
        """)
    void testCase23() {
        initTodayDateTime("2024-01-15T12:00:00");
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDealBy(1L,"AFKS", "10:00:00", 16D, 160000D, 10),
                buildDealBy(1L,"AFKS", "11:00:00", 17D, 85000D, 5),
                buildDealBy(1L,"AFKS", "12:00:00", 19D, 26000D, 2)
            )
        );
        exchangeDataFixture().initTradingResults(
            List.of(
                buildDealResultBy("AFKS", "2024-01-11", 14D, 15D, 14D, 212421D),
                buildDealResultBy("AFKS", "2024-01-12", 16D, 14D, 15D, 123521D),
                buildDealResultBy("AFKS", "2024-01-15", 17D, 18D, 17D, 623554D)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().tradingDataIntegrate();

        List<InstrumentStatistic> instrumentStatistics = exchangeManager().getStatistics();

        assertEquals(1, instrumentStatistics.size());
        assertEquals(271000D, instrumentStatistics.get(0).getTodayValue());
        assertEquals(123521D, instrumentStatistics.get(0).getHistoryMedianValue());
        assertEquals(16D, instrumentStatistics.get(0).getTodayOpenPrice());
        assertEquals(19D, instrumentStatistics.get(0).getTodayLastPrice());
        assertTrue(instrumentStatistics.get(0).isRiseForPrevDay(0.01));
    }

    @Test
    @DisplayName("""
        T24. Хранилище финансовых инструментов не пустое.
        Текущий день недели - понедельник. Данные по сделкам есть.
        Исторические данные загружены, но есть пропуск в днях.
        Результат: статистика посчитана, но определить рост в предыдущий день нельзя.
        """)
    void testCase24() {
        initTodayDateTime("2024-01-17T12:00:00");
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDealBy(1L,"AFKS", "10:00:00", 16D, 160000D, 10),
                buildDealBy(1L,"AFKS", "11:00:00", 17D, 85000D, 5),
                buildDealBy(1L,"AFKS", "12:00:00", 19D, 26000D, 2)
            )
        );
        exchangeDataFixture().initTradingResults(
            List.of(
                buildDealResultBy("AFKS", "2024-01-11", 14D, 15D, 14D, 212421D),
                buildDealResultBy("AFKS", "2024-01-12", 16D, 14D, 15D, 123521D),
                buildDealResultBy("AFKS", "2024-01-16", 17D, 18D, 17D, 623554D)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().tradingDataIntegrate();

        List<InstrumentStatistic> instrumentStatistics = exchangeManager().getStatistics();

        assertEquals(1, instrumentStatistics.size());
        assertEquals(271000D, instrumentStatistics.get(0).getTodayValue());
        assertEquals(123521D, instrumentStatistics.get(0).getHistoryMedianValue());
        assertEquals(16D, instrumentStatistics.get(0).getTodayOpenPrice());
        assertEquals(19D, instrumentStatistics.get(0).getTodayLastPrice());
        var error = assertThrows(DomainException.class, () -> instrumentStatistics.get(0).isRiseForPrevDay(0.01));
        assertEquals("Нет данных по итогам торгов за 2024-01-15.", error.getMessage());
    }
}
