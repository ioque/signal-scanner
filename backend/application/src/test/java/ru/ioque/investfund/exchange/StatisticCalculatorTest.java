package ru.ioque.investfund.exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

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
        T1. Хранилище финансовых инструментов не пустое.
        Сохранены торговые данные только по акции AFKS.
        Запрошена статистика по инструментам.
        Результат: статистика успешно расчитана только для акции AFKS.
        """)
    void testCase1() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildBuyDealBy(1L,"AFKS", "10:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"AFKS", "11:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"AFKS", "12:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"AFKS", "13:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"AFKS", "14:00:00", 10D, 10D, 1)
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
        exchangeManager().integrateTradingData();

        statisticManager().calcStatistic();

        InstrumentStatistic statistic = statisticRepository().getBy(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).findFirst().orElseThrow());
        assertEquals(50D, statistic.getTodayValue());
        assertEquals(10D, statistic.getHistoryMedianValue());
        assertEquals(10D, statistic.getTodayOpenPrice());
        assertEquals(10D, statistic.getTodayLastPrice());
        assertEquals(4, statistic.getClosePriceSeries().size());
        assertEquals(4, statistic.getOpenPriceSeries().size());
        assertEquals(4, statistic.getWaPriceSeries().size());
        assertEquals(4, statistic.getValueSeries().size());
        assertEquals(5, statistic.getTodayPriceSeries().size());
    }

    @Test
    @DisplayName("""
        T2. Хранилище финансовых инструментов не пустое.
        Сохранены торговые данные только по фьючерсу BRF4.
        Запрошена статистика по инструментам.
        Результат: статистика успешно расчитана только для фьючерса BRF4. Значений по средневзвешенной цене нет.
        """)
    void testCase2() {
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
        exchangeManager().integrateTradingData();

        statisticManager().calcStatistic();

        InstrumentStatistic statistic = statisticRepository().getBy(getInstrumentsBy(List.of("BRF4")).map(Instrument::getId).findFirst().orElseThrow());
        assertEquals(500000D, statistic.getTodayValue());
        assertEquals(10D, statistic.getHistoryMedianValue());
        assertEquals(10D, statistic.getTodayOpenPrice());
        assertEquals(10D, statistic.getTodayLastPrice());
        assertEquals(4, statistic.getClosePriceSeries().size());
        assertEquals(4, statistic.getOpenPriceSeries().size());
        assertEquals(0, statistic.getWaPriceSeries().size());
        assertEquals(4, statistic.getValueSeries().size());
        assertEquals(5, statistic.getTodayPriceSeries().size());
    }

    @Test
    @DisplayName("""
        T3. Хранилище финансовых инструментов не пустое.
        Сохранены торговые данные только по валютной паре USD000UTSTOM.
        Запрошена статистика по инструментам.
        Результат: статистика успешно расчитана только для валютной пары USD000UTSTOM.
        """)
    void testCase3() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildBuyDealBy(1L,"USD000UTSTOM", "10:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"USD000UTSTOM", "11:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"USD000UTSTOM", "12:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"USD000UTSTOM", "13:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"USD000UTSTOM", "14:00:00", 10D, 10D, 1)
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
        exchangeManager().integrateTradingData();

        statisticManager().calcStatistic();

        InstrumentStatistic statistic = statisticRepository().getBy(getInstrumentsBy(List.of("USD000UTSTOM")).map(Instrument::getId).findFirst().orElseThrow());
        assertEquals(50D, statistic.getTodayValue());
        assertEquals(10D, statistic.getHistoryMedianValue());
        assertEquals(10D, statistic.getTodayOpenPrice());
        assertEquals(10D, statistic.getTodayLastPrice());
        assertEquals(4, statistic.getClosePriceSeries().size());
        assertEquals(4, statistic.getOpenPriceSeries().size());
        assertEquals(4, statistic.getWaPriceSeries().size());
        assertEquals(4, statistic.getValueSeries().size());
        assertEquals(5, statistic.getTodayPriceSeries().size());
    }

    @Test
    @DisplayName("""
        T4. Хранилище финансовых инструментов не пустое.
        Сохранены торговые данные только по индексу фондового рынка IMOEX.
        Запрошена статистика по инструментам.
        Результат: статистика успешно расчитана только для индекса фондового рынка IMOEX. Значений по средневзвешенной цене нет.
        """)
    void testCase4() {
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
        exchangeManager().integrateTradingData();

        statisticManager().calcStatistic();

        InstrumentStatistic statistic = statisticRepository().getBy(getInstrumentsBy(List.of("IMOEX")).map(Instrument::getId).findFirst().orElseThrow());
        assertEquals(50D, statistic.getTodayValue());
        assertEquals(10D, statistic.getHistoryMedianValue());
        assertEquals(10D, statistic.getTodayOpenPrice());
        assertEquals(10D, statistic.getTodayLastPrice());
        assertEquals(4, statistic.getClosePriceSeries().size());
        assertEquals(4, statistic.getOpenPriceSeries().size());
        assertEquals(0, statistic.getWaPriceSeries().size());
        assertEquals(4, statistic.getValueSeries().size());
        assertEquals(5, statistic.getTodayPriceSeries().size());
    }

    @Test
    @DisplayName("""
        T5. Хранилище финансовых инструментов не пустое.
        Торговых данных нет.
        Запрошена статистика по инструментам.
        Результат: нет статистических данных.
        """)
    void testCase5() {
        assertEquals(0, exchangeManager().getStatistics().size());
    }

    @Test
    @DisplayName("""
        T6. Хранилище финансовых инструментов не пустое.
        Сохранена только история торгов по инструменту AFKS.
        Запрошена статистика по инструментам.
        Результат: нет статистических данных.
        """)
    void testCase6() {
        exchangeDataFixture().initTradingResults(
            List.of(
                buildDealResultBy("AFKS", "2024-01-03", 10D, 10D, 10D, 10D),
                buildDealResultBy("AFKS", "2024-01-04", 10D, 10D, 10D, 10D),
                buildDealResultBy("AFKS", "2024-01-05", 10D, 10D, 10D, 10D),
                buildDealResultBy("AFKS", "2024-01-06", 10D, 10D, 10D, 10D)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().integrateTradingData();
        assertEquals(0, exchangeManager().getStatistics().size());
    }

    @Test
    @DisplayName("""
        T7. Хранилище финансовых инструментов не пустое.
        Сохранены только данные по текущим торгам.
        Запрошена статистика по инструментам.
        Результат: нет статистических данных.
        """)
    void testCase7() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildBuyDealBy(1L,"AFKS", "10:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"AFKS", "11:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"AFKS", "12:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"AFKS", "13:00:00", 10D, 10D, 1),
                buildBuyDealBy(1L,"AFKS", "14:00:00", 10D, 10D, 1)
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().integrateTradingData();
        assertEquals(0, exchangeManager().getStatistics().size());
    }

    @Test
    @DisplayName("""
        T8. Хранилище финансовых инструментов не пустое.
        Текущий день недели - понедельник. Данные по сделкам есть.
        Исторические данные загружены.
        Результат: статистика посчитана. Рост за предыдущий день определяется.
        """)
    void testCase8() {
        initTodayDateTime("2024-01-15T12:00:00");
        exchangeDataFixture().initDealDatas(
            List.of(
                buildBuyDealBy(1L,"AFKS", "10:00:00", 16D, 160000D, 10),
                buildBuyDealBy(1L,"AFKS", "11:00:00", 17D, 85000D, 5),
                buildBuyDealBy(1L,"AFKS", "12:00:00", 19D, 26000D, 2)
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
        exchangeManager().integrateTradingData();

        statisticManager().calcStatistic();

        InstrumentStatistic statistic = statisticRepository().getBy(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).findFirst().orElseThrow());
        assertEquals(271000D, statistic.getTodayValue());
        assertEquals(123521D, statistic.getHistoryMedianValue());
        assertEquals(16D, statistic.getTodayOpenPrice());
        assertEquals(19D, statistic.getTodayLastPrice());
        assertTrue(statistic.isRiseForPrevDay(0.01));
    }

    @Test
    @DisplayName("""
        T9. Хранилище финансовых инструментов не пустое.
        Текущий день недели - понедельник. Данные по сделкам есть.
        Исторические данные загружены, но есть пропуск в днях.
        Результат: статистика посчитана, но определить рост в предыдущий день нельзя.
        """)
    void testCase9() {
        initTodayDateTime("2024-01-17T12:00:00");
        exchangeDataFixture().initDealDatas(
            List.of(
                buildBuyDealBy(1L,"AFKS", "10:00:00", 16D, 160000D, 10),
                buildBuyDealBy(1L,"AFKS", "11:00:00", 17D, 85000D, 5),
                buildBuyDealBy(1L,"AFKS", "12:00:00", 19D, 26000D, 2)
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
        exchangeManager().integrateTradingData();

        statisticManager().calcStatistic();

        InstrumentStatistic statistic = statisticRepository().getBy(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).findFirst().orElseThrow());
        assertEquals(271000D, statistic.getTodayValue());
        assertEquals(123521D, statistic.getHistoryMedianValue());
        assertEquals(16D, statistic.getTodayOpenPrice());
        assertEquals(19D, statistic.getTodayLastPrice());
        var error = assertThrows(DomainException.class, () -> statistic.isRiseForPrevDay(0.01));
        assertEquals("Нет данных по итогам торгов за 2024-01-15.", error.getMessage());
    }

    @Test
    @DisplayName(
        """
        T10. Биржа не зарегистрирована. Запрос статистики.
        Результат: ошибка, "Биржа не зарегистрирована".
        """
    )
    void testCase10() {
        exchangeRepository().clear();

        var error = assertThrows(ApplicationException.class, () -> exchangeManager().getStatistics());
        assertEquals("Биржа не зарегистрирована.", error.getMessage());
    }


    @Test
    @Disabled
    @DisplayName("""
        T11. Успешный расчет статистических данных создает событие "Статистика обновлена"
        """)
    void testCase11() {

    }
}
