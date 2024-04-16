package ru.ioque.investfund.datasource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.command.DisableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.datasource.command.UnregisterDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Stock;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DATASOURCE MANAGER TEST - INTEGRATION")
public class DatasourceIntegrationTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
    }

    @Test
    @DisplayName("""
        T1. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Запускается интеграция с источником биржевых данных. Дублей по тикерам в данных нет, все записи валидны.
        """)
    void testCase1() {
        initTodayDateTime("2023-12-12T10:00:00");
        initInstruments(
            imoex(),
            afks()
        );

        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));

        final Optional<Datasource> exchange = datasourceRepository().getBy(getDatasourceId());
        assertTrue(exchange.isPresent());
        assertEquals("Московская биржа", exchange.get().getName());
        assertEquals("http://localhost:8080", exchange.get().getUrl());
        assertEquals("Московская биржа", exchange.get().getDescription());
        assertEquals(2, exchange.get().getInstruments().size());
        assertEquals(2, getInstruments(getDatasourceId()).size());
    }

    @Test
    @DisplayName("""
        T2. Повторная интеграция с источником биржевых данных.
        Количество инструментов при первой синхронизации - 10, при второй - те же 10.
        Результат: в системе зарегистрирвана одна биржа, количество сохраненных инструментов 10.
        """)
    void testCase2() {
        initTodayDateTime("2023-12-12T10:00:00");
        initInstruments(afks(), imoex(), brf4());
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        clearLogs();

        final var id = datasourceRepository().getBy(getDatasourceId()).orElseThrow().getId();
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        assertEquals(3, getInstruments(getDatasourceId()).size());
        assertEquals(id, datasourceRepository().getBy(getDatasourceId()).orElseThrow().getId());
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
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        clearLogs();
        initInstruments(afks(), imoex(), brf4(), lkoh(), rosn(), sibn());
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        assertEquals(6, getInstruments(getDatasourceId()).size());
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
        datasourceStorage().initInstruments(List.of(
            afks(),
            Stock.builder()
                .id(UUID.randomUUID())
                .datasourceId(getDatasourceId())
                .shortName("ао Система")
                .name("АФК Система1")
                .ticker("AFKS")
                .lotSize(10000)
                .build()
        ));
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        assertEquals(1, getInstruments(getDatasourceId()).size());
    }

    @Test
    @DisplayName("""
        T5. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных по инструменту AFKS.
        Все данные источника валидны, дублирующих записей нет.
        Результат: сохранены текущие сделки и история торгов.
        """)
    void testCase5() {
        final UUID datasourceId = getDatasourceId();
        datasourceStorage().initInstruments(List.of(afks()));
        initTodayDateTime("2023-12-08T10:15:00");
        commandBus().execute(new IntegrateInstrumentsCommand(datasourceId));
        clearLogs();
        initDealDatas(
            buildDealWith(datasourceId, 1L, "AFKS", LocalDateTime.parse("2023-12-08T10:00:00")),
            buildDealWith(datasourceId, 2L, "AFKS", LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith(datasourceId, 3L, "AFKS", LocalDateTime.parse("2023-12-08T10:15:00"))
        );
        initTradingResults(
            buildTradingResultWith(datasourceId, "AFKS", LocalDate.parse("2023-12-08")).build(),
            buildTradingResultWith(datasourceId, "AFKS", LocalDate.parse("2023-12-09")).build(),
            buildTradingResultWith(datasourceId, "AFKS", LocalDate.parse("2023-12-10")).build(),
            buildTradingResultWith(datasourceId, "AFKS", LocalDate.parse("2023-12-11")).build()
        );

        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));

        assertEquals(1, getInstruments(datasourceId).size());
        assertEquals(3, getIntradayValuesBy(datasourceId).size());
        assertEquals(4, getHistoryValues(datasourceId).size());
    }

    @Test
    @DisplayName("""
        T9. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных инструмента AFKS.
        Текущая дата 2023-12-19T10:00:00, неторговый день, сделок нет.
        Результат: Загружены данные итогов торгов.
        """)
    void testCase9() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-19T10:00:00");
        integrateInstruments(datasourceId, afks());
        clearLogs();
        initTradingResults(generateTradingResultsBy(
            datasourceId,
            "AFKS",
            dateTimeProvider().monthsAgo(6),
            dateTimeProvider().daysAgo(1)
        ));

        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));

        assertEquals(
            131,
            getHistoryValuesBy(datasourceId, "AFKS").size()
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T10:15:00");
        integrateInstruments(datasourceId, afks());
        clearLogs();
        initDealDatas(
            buildDealWith(datasourceId, 1L, "AFKS", LocalDateTime.parse("2023-12-08T10:00:00")),
            buildDealWith(datasourceId, 2L, "AFKS", LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith(datasourceId, 2L, "AFKS", LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith(datasourceId, 2L, "AFKS", LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith(datasourceId, 5L, "AFKS", LocalDateTime.parse("2023-12-08T10:15:00"))
        );

        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));

        assertEquals(3, getIntradayValuesBy(datasourceId, "AFKS").size());
    }

    @Test
    @DisplayName("""
        T11. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        По инструменту AFKS сохранены сделки до 2023-12-07T12:00:00.
        Текущее время 2023-12-07T13:00:00. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: сохранены только новые сделки, прошедшие после 12:00:00.
        """)
    void testCas11() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-07T12:00:00");
        integrateInstruments(datasourceId, afks());
        initDealDatas(
            buildDealWith(datasourceId, 1L, "AFKS", LocalDateTime.parse("2023-12-07T11:00:00")),
            buildDealWith(datasourceId, 2L, "AFKS", LocalDateTime.parse("2023-12-07T11:30:00"))
        );
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));
        clearLogs();
        initTodayDateTime("2023-12-07T13:00:00");
        initDealDatas(
            buildDealWith(datasourceId, 1L, "AFKS", LocalDateTime.parse("2023-12-07T11:00:00")),
            buildDealWith(datasourceId, 2L, "AFKS", LocalDateTime.parse("2023-12-07T11:30:00")),
            buildDealWith(datasourceId, 3L, "AFKS", LocalDateTime.parse("2023-12-07T12:10:00")),
            buildDealWith(datasourceId, 4L, "AFKS", LocalDateTime.parse("2023-12-07T12:30:00")),
            buildDealWith(datasourceId, 5L, "AFKS", LocalDateTime.parse("2023-12-07T12:40:00"))
        );

        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));

        assertEquals(5, getIntradayValuesBy(datasourceId, "AFKS").size());
    }

    @Test
    @DisplayName("""
        T12. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        По инструменту AFKS сохранены итоги торгов по 2023-12-07.
        Текущее время 2023-12-09T13:00:00. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: добавлена история торгов за 2023-12-08.
        """)
    void testCas12() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(datasourceId, afks());
        initTradingResults(generateTradingResultsBy(datasourceId, "AFKS", nowMinus3Month(), nowMinus1Days()));
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));
        clearLogs();
        initTodayDateTime("2023-12-09T13:00:00");
        initTradingResults(generateTradingResultsBy(datasourceId, "AFKS", nowMinus3Month(), nowMinus1Days()));

        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));

        assertEquals(66, getHistoryValuesBy(datasourceId, "AFKS").size());
    }

    @Test
    @DisplayName("""
        T13. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Обновление инструмента AFKS не включено. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: торговые данные не загружены.
        """)
    void testCase13() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(datasourceId, afks());
        initDealDatas(buildDealWith(datasourceId, 1L, "AFKS", LocalDateTime.parse("2023-12-07T11:00:00")));
        initTradingResults(buildDealResultBy(datasourceId, "AFKS", "2024-01-03", 10D, 10D, 10D, 10D));

        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));

        assertEquals(0, getHistoryValues(datasourceId).size());
        assertEquals(0, getIntradayValuesBy(datasourceId).size());
    }

    @Test
    @DisplayName("""
        T14. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Данные торгов успешно проинтегрированы, обновление инструмента включено.
        Обновление инструмента выключается и запускается интеграция торговых данных.
        Результат: новые торговые данные не загружены.
        """)
    void testCase14() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(datasourceId, afks());
        initDealDatas(buildBuyDealBy(datasourceId, 1L, "AFKS", "10:00:00", 10D, 10D, 1));
        initTradingResults(buildDealResultBy(datasourceId, "AFKS", "2023-12-07", 10D, 10D, 10D, 10D));
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));
        clearLogs();
        initDealDatas(
            buildBuyDealBy(datasourceId, 1L, "AFKS", "10:00:00", 10D, 10D, 1),
            buildBuyDealBy(datasourceId, 1L, "AFKS", "11:00:00", 10D, 10D, 1)
        );
        initTradingResults(
            buildDealResultBy(datasourceId, "AFKS", "2023-12-06", 10D, 10D, 10D, 10D),
            buildDealResultBy(datasourceId, "AFKS", "2023-12-07", 10D, 10D, 10D, 10D)
        );

        commandBus().execute(new DisableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));

        assertEquals(1, getHistoryValuesBy(datasourceId, "AFKS").size());
        assertEquals(1, getIntradayValuesBy(datasourceId, "AFKS").size());
    }

    @Test
    @DisplayName("""
        T15. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Попытка включить обновление по списку идентификаторов.
        Результат: ошибка, "Биржа не зарегистрирована".
        """)
    void testCase15() {
        UUID datasourceId = getDatasourceId();
        commandBus().execute(new UnregisterDatasourceCommand(datasourceId));
        var error = assertThrows(
            IllegalArgumentException.class,
            () -> commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")))
        );
        assertEquals(String.format("Источник данных[id=%s] не существует.", datasourceId), error.getMessage());
    }

    @Test
    @DisplayName("""
        T16. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Попытка выключить обновление по списку идентификаторов.
        Результат: ошибка, "Биржа не зарегистрирована".
        """)
    void testCase16() {
        UUID datasourceId = getDatasourceId();
        commandBus().execute(new UnregisterDatasourceCommand(datasourceId));
        var error = assertThrows(
            IllegalArgumentException.class,
            () -> commandBus().execute(new DisableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")))
        );
        assertEquals(String.format("Источник данных[id=%s] не существует.", datasourceId), error.getMessage());
    }

    @Test
    @DisplayName("""
        T17. После успешной интеграции торговых данных создается событие "Торговые данные обновлены".
        """)
    void testCase17() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(datasourceId, afks());
        initDealDatas(buildBuyDealBy(datasourceId, 1L, "AFKS", "10:00:00", 10D, 10D, 1));
        initTradingResults(buildDealResultBy(datasourceId, "AFKS", "2023-12-07", 10D, 10D, 10D, 10D));
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));
        assertEquals(TradingDataUpdatedEvent.class, eventPublisher().getEvents().get(0).getClass());
    }

    @Test
    @DisplayName("""
        T18. У сделок одинаковое время, но разные номера. Все сделки сохранены.
        """)
    void testCase18() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T10:15:00");
        integrateInstruments(datasourceId, afks());
        initTradingResults(buildDealResultBy(datasourceId, "AFKS", "2023-12-07", 10D, 10D, 10D, 10D));
        initDealDatas(
            buildBuyDealBy(datasourceId, 1L, "AFKS", "10:00:00", 10D, 10D, 1),
            buildBuyDealBy(datasourceId, 2L, "AFKS", "10:00:00", 11D, 10D, 1),
            buildBuyDealBy(datasourceId, 3L, "AFKS", "10:00:00", 11D, 10D, 2)
        );
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));
        assertEquals(3, getIntradayValuesBy(datasourceId, "AFKS").size());
    }

    @Test
    @DisplayName("""
        T19. Одна и та же сделка интегрируется дважды. Сохранена одна сделка.
        """)
    void testCase19() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T10:15:00");
        integrateInstruments(datasourceId, afks());
        initTradingResults(buildDealResultBy(datasourceId, "AFKS", "2023-12-07", 10D, 10D, 10D, 10D));
        initDealDatas(buildBuyDealBy(datasourceId, 1L, "AFKS", "10:00:00", 10D, 10D, 1));
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, List.of("AFKS")));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));
        assertEquals(1, getIntradayValuesBy(datasourceId, "AFKS").size());
    }
}
