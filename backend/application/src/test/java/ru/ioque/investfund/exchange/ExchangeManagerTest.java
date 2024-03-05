package ru.ioque.investfund.exchange;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EXCHANGE MANAGER - INTEGRATION")
public class ExchangeManagerTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Запускается интеграция с источником биржевых данных.
        Дублей по тикерам в данных нет, все записи валидны.
        Данные представлены по двум инструментам - IMOEX, AFKS.
        Результат: зарегистрирована биржа, сохранены 2 инструмента.
        """)
    void testCase1() {
        initTodayDateTime("2023-12-12T10:00:00");
        initInstruments(
            imoex(),
            afks()
        );

        exchangeManager().integrateWithDataSource();
        final Optional<Exchange> exchange = exchangeRepository().getBy(dateTimeProvider().nowDate());
        assertTrue(exchange.isPresent());
        assertEquals("Московская биржа", exchange.get().getName());
        assertEquals("http://localhost:8081", exchange.get().getUrl());
        assertEquals("Московская биржа", exchange.get().getDescription());
        assertEquals(2, exchange.get().getInstruments().size());
        assertEquals(2, getInstruments().size());
        assertEquals(2, loggerProvider().log.size());
        assertTrue(
            loggerProvider()
                .logContainsMessageParts(
                    "Начата синхронизация с источником данных \"Московская биржа\". Текущее время: 2023-12-12T10:00.",
                    "Завершена синхронизация с источником данных \"Московская биржа\". Текущее время: 2023-12-12T10:00."
                )
        );
    }

    @Test
    @DisplayName("""
        T2. Повторная интеграция с источником биржевых данных.
        Количество инструментов при первой синхронизации - 10, при второй - те же 10.
        Результат: в системе зарегистрирвана одна биржа, количество сохраненных инструментов 10.
        """)
    void testCase2() {
        initTodayDateTime("2023-12-12T10:00:00");
        exchangeManager().integrateWithDataSource();
        clearLogs();

        final var id = exchangeRepository().getBy(dateTimeProvider().nowDate()).orElseThrow().getId();
        exchangeManager().integrateWithDataSource();
        assertEquals(10, getInstruments().size());
        assertEquals(id, exchangeRepository().getBy(dateTimeProvider().nowDate()).orElseThrow().getId());
    }

    @Test
    @DisplayName("""
        T3. Повторная интеграция с источником биржевых данных.
        Количество инструментов при первой синхронизации - 3, при второй - 6.
        Результат: в системе зарегистрирована одна биржа, количество сохраненных инструментов 6.
        """)
    void testCase3() {
        initInstruments(afks(), imoex(), brf4());
        initTodayDateTime("2023-12-12T10:00:00");
        exchangeManager().integrateWithDataSource();
        clearLogs();
        initInstruments(afks(), imoex(), brf4(), lkoh(), rosn(), sibn());
        exchangeManager().integrateWithDataSource();
        assertEquals(6, getInstruments().size());
    }

    @Test
    @DisplayName("""
        T4. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Запускается интеграция с источником биржевых данных.
        Есть дубли по тикерам, все записи валидны.
        Результат: зарегистрирована биржа, в хранилище инструментов нет дубликатов.
        """)
    void testCase4() {
        initTodayDateTime("2023-12-12T10:00:00");
        exchangeDataFixture().initInstruments(List.of(
            afks(),
            Stock.builder()
                .id(UUID.randomUUID())
                .shortName("ао Система")
                .name("АФК Система1")
                .ticker("AFKS")
                .lotSize(10000)
                .build()
        ));
        exchangeManager().integrateWithDataSource();
        assertEquals(1, getInstruments().size());
        assertEquals(2, loggerProvider().log.size());
        assertTrue(loggerProvider().logContainsMessageParts(
            "Начата синхронизация с источником данных \"Московская биржа\". Текущее время: 2023-12-12T10:00.",
            "Завершена синхронизация с источником данных \"Московская биржа\". Текущее время: 2023-12-12T10:00."
        ));
    }

    @Test
    @DisplayName("""
        T5. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных по инструменту AFKS.
        Все данные источника валидны, дублирующих записей нет.
        Результат: сохранены текущие сделки и история торгов.
        """)
    void testCase5() {
        exchangeDataFixture().initInstruments(List.of(afks()));
        dateTimeProvider().setNow(LocalDateTime.parse("2023-12-08T10:15:00"));
        exchangeManager().integrateWithDataSource();
        clearLogs();
        initDealDatas(
                buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T10:00:00")),
                buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T10:03:00")),
                buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T10:15:00"))
        );
        initTradingResults(
                buildTradingResultWith("AFKS", LocalDate.parse("2023-12-08")).build(),
                buildTradingResultWith("AFKS", LocalDate.parse("2023-12-09")).build(),
                buildTradingResultWith("AFKS", LocalDate.parse("2023-12-10")).build(),
                buildTradingResultWith("AFKS", LocalDate.parse("2023-12-11")).build()
        );

        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().integrateTradingData();
        final var instrument = getInstruments().get(0);
        assertEquals(1, getInstruments().size());
        assertEquals(3, instrument.getIntradayValues().size());
        assertEquals(4, instrument.getDailyValues().size());
        assertEquals(2, loggerProvider().log.size());
        assertTrue(
            loggerProvider()
                .logContainsMessageParts(
                    "Начато обновление торговых данных инструмента AFKS, 2023-12-08T10:15. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: <_> - <_>.",
                    "Завершено обновление торговых данных инструмента AFKS, 2023-12-08T10:15. Текущее количество сделок 3, интервал сделок: 2023-12-08T10:00 - 2023-12-08T10:15. Текущий период исторических данных: 2023-12-08 - 2023-12-11."
                )
        );
    }

    @Test
    @DisplayName("""
        T9. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных инструмента AFKS.
        Текущая дата 2023-12-19T10:00:00, неторговый день, сделок нет.
        Результат: Загружены данные итогов торгов.
        """)
    void testCase9() {
        initTodayDateTime("2023-12-19T10:00:00");
        integrateInstruments(afks());
        clearLogs();
        initTradingResults(generateTradingResultsBy(
            "AFKS",
            dateTimeProvider().monthsAgo(6),
            dateTimeProvider().daysAgo(1)
        ));

        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().integrateTradingData();

        assertEquals(
            131,
            getDailyTradingResultsBy("AFKS").size()
        );
        assertEquals(2, loggerProvider().log.size());
        assertTrue(
            loggerProvider()
                .logContainsMessageParts(
                    "Начато обновление торговых данных инструмента AFKS, 2023-12-19T10:00. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: <_> - <_>.",
                    "Завершено обновление торговых данных инструмента AFKS, 2023-12-19T10:00. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: 2023-06-19 - 2023-12-18."
                )
        );
    }

    @Test
    @DisplayName("""
        T10. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных инструмента AFKS.
        В данных по сделкам есть дублирующие записи.
        Результат: дубликаты не сохранены, остальные сделки успешно сохранены.
        """)
    void testCase10() {
        initTodayDateTime("2023-12-08T10:15:00");
        integrateInstruments(afks());
        clearLogs();
        initDealDatas(
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T10:00:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T10:15:00"))
        );

        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().integrateTradingData();

        assertEquals(3, getIntradayValue("AFKS").size());
        assertEquals(2, loggerProvider().log.size());
        assertTrue(
            loggerProvider()
                .logContainsMessageParts(
                    "Начато обновление торговых данных инструмента AFKS, 2023-12-08T10:15. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: <_> - <_>.",
                    "Завершено обновление торговых данных инструмента AFKS, 2023-12-08T10:15. Текущее количество сделок 3, интервал сделок: 2023-12-08T10:00 - 2023-12-08T10:15. Текущий период исторических данных: <_> - <_>."
                )
        );
    }

    @Test
    @DisplayName("""
        T11. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных инструмента AFKS.
        В исходных данных по сделкам есть данные за предыдущий день.
        Результат: сохранены только сделки за текущий день.
        """)
    void testCase11() {
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(afks());
        clearLogs();
        initDealDatas(
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T11:10:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T11:20:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T10:10:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T11:12:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-08T11:35:00"))
        );

        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().integrateTradingData();

        assertEquals(3, getIntradayValue("AFKS").size());
        assertEquals(2, loggerProvider().log.size());
        assertTrue(
            loggerProvider()
                .logContainsMessageParts(
                    "Начато обновление торговых данных инструмента AFKS, 2023-12-08T12:00. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: <_> - <_>.",
                    "Завершено обновление торговых данных инструмента AFKS, 2023-12-08T12:00. Текущее количество сделок 3, интервал сделок: 2023-12-08T10:10 - 2023-12-08T11:35. Текущий период исторических данных: <_> - <_>."
                )
        );
    }

    @Test
    @DisplayName("""
        T12. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        По инструменту AFKS сохранены сделки до 2023-12-07T12:00:00.
        Текущее время 2023-12-07T13:00:00. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: сохранены только новые сделки, прошедшие после 12:00:00.
        """)
    void testCas12() {
        initTodayDateTime("2023-12-07T12:00:00");
        integrateInstruments(afks());
        initDealDatas(
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T11:00:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T11:30:00"))
        );
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().integrateTradingData();
        clearLogs();
        initTodayDateTime("2023-12-07T13:00:00");
        initDealDatas(
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T11:00:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T11:30:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T12:10:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T12:30:00")),
            buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T12:40:00"))
        );

        exchangeManager().integrateTradingData();

        assertEquals(5, getIntradayValue("AFKS").size());
        assertTrue(
            loggerProvider()
                .logContainsMessageParts(
                    "Начато обновление торговых данных инструмента AFKS, 2023-12-07T13:00. Текущее количество сделок 2, интервал сделок: 2023-12-07T11:00 - 2023-12-07T11:30. Текущий период исторических данных: <_> - <_>.",
                    "Завершено обновление торговых данных инструмента AFKS, 2023-12-07T13:00. Текущее количество сделок 5, интервал сделок: 2023-12-07T11:00 - 2023-12-07T12:40. Текущий период исторических данных: <_> - <_>."
                )
        );
    }

    @Test
    @DisplayName("""
        T13. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        По инструменту AFKS сохранены итоги торгов по 2023-12-07.
        Текущее время 2023-12-09T13:00:00. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: добавлена история торгов за 2023-12-08.
        """)
    void testCas13() {
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(afks());
        initTradingResults(generateTradingResultsBy("AFKS", nowMinus3Month(), nowMinus1Days()));
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().integrateTradingData();
        clearLogs();
        initTodayDateTime("2023-12-09T13:00:00");
        initTradingResults(generateTradingResultsBy("AFKS", nowMinus3Month(), nowMinus1Days()));

        exchangeManager().integrateTradingData();

        assertEquals(66, getDailyTradingResultsBy("AFKS").size());
        assertTrue(
            loggerProvider()
                .logContainsMessageParts(
                    "Начато обновление торговых данных инструмента AFKS, 2023-12-09T13:00. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: 2023-09-08 - 2023-12-07.",
                    "Завершено обновление торговых данных инструмента AFKS, 2023-12-09T13:00. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: 2023-09-08 - 2023-12-08."
                )
        );
    }

    @Test
    @DisplayName("""
        T14. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Обновление инструмента AFKS не включено. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: торговые данные не загружены.
        """)
    void testCase14() {
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(afks());
        initDealDatas(buildDealWith("AFKS", LocalDateTime.parse("2023-12-07T11:00:00")));
        initTradingResults(buildDealResultBy("AFKS", "2024-01-03", 10D, 10D, 10D, 10D));
        exchangeManager().integrateTradingData();

        assertEquals(0, getDailyTradingResultsBy("AFKS").size());
        assertEquals(0, getIntradayValue("AFKS").size());
    }

    @Test
    @DisplayName("""
        T15. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Данные торгов успешно проинтегрированы, обновление инструмента включено.
        Обновление инструмента выключается и запускается интеграция торговых данных.
        Результат: новые торговые данные не загружены.
        """)
    void testCase15() {
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(afks());
        initDealDatas(buildBuyDealBy(1L,"AFKS", "10:00:00", 10D, 10D, 1));
        initTradingResults(buildDealResultBy("AFKS", "2023-12-07", 10D, 10D, 10D, 10D));
        exchangeManager().enableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        exchangeManager().integrateTradingData();
        clearLogs();

        exchangeManager().disableUpdate(getInstrumentsBy(List.of("AFKS")).map(Instrument::getId).toList());
        initDealDatas(
            buildBuyDealBy(1L,"AFKS", "10:00:00", 10D, 10D, 1),
            buildBuyDealBy(1L,"AFKS", "11:00:00", 10D, 10D, 1)
        );
        initTradingResults(
            buildDealResultBy("AFKS", "2023-12-06", 10D, 10D, 10D, 10D),
            buildDealResultBy("AFKS", "2023-12-07", 10D, 10D, 10D, 10D)
        );

        exchangeManager().integrateTradingData();

        assertEquals(1, getDailyTradingResultsBy("AFKS").size());
        assertEquals(1, getIntradayValue("AFKS").size());
    }

    @Test
    @DisplayName("""
        T16. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Попытка включить обновление по списку идентификаторов.
        Результат: ошибка, "Биржа не зарегистрирована".
        """)
    void testCase16() {
        var error = assertThrows(ApplicationException.class, () -> exchangeManager().enableUpdate(List.of(UUID.randomUUID())));
        assertEquals("Биржа не зарегистрирована.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T17. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Попытка выключить обновление по списку идентификаторов.
        Результат: ошибка, "Биржа не зарегистрирована".
        """)
    void testCase17() {
        var error = assertThrows(ApplicationException.class, () -> exchangeManager().disableUpdate(List.of(UUID.randomUUID())));
        assertEquals("Биржа не зарегистрирована.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T18. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Попытка включить интеграцию торговых данных.
        Результат: ошибка, "Биржа не зарегистрирована".
        """)
    void testCase18() {
        var error = assertThrows(ApplicationException.class, () -> exchangeManager().integrateTradingData());
        assertEquals("Биржа не зарегистрирована.", error.getMessage());
    }

    @Test
    @Disabled
    @DisplayName("""
        T19. После успешной синхронизации создается событие "Синхронизация завершена".
        """)
    void testCase19() {

    }

    @Test
    @Disabled
    @DisplayName("""
        T20. После успешной интеграции торговых данных создается событие "Интеграция торговых данных завершена".
        """)
    void testCase20() {

    }
}
