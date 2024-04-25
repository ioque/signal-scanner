package ru.ioque.investfund.datasource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.application.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.application.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.application.datasource.command.UnregisterDatasourceCommand;
import ru.ioque.investfund.application.datasource.event.TradingDataIntegrated;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DATASOURCE MANAGER TEST - INTEGRATION")
public class DatasourceIntegrationTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasourceCommand.builder()
                .track(UUID.randomUUID())
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
    }

    @Test
    @DisplayName("""
        T5. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных по инструменту AFKS.
        Все данные источника валидны, дублирующих записей нет.
        Результат: сохранены текущие сделки и история торгов.
        """)
    void testCase5() {
        final DatasourceId datasourceId = getDatasourceId();
        datasourceStorage().initInstrumentDetails(List.of(afks()));
        initTodayDateTime("2023-12-08T10:15:00");
        commandBus().execute(new IntegrateInstrumentsCommand(UUID.randomUUID(), datasourceId));
        clearLogs();
        initIntradayValues(
            buildDealWith(AFKS,1L, LocalDateTime.parse("2023-12-08T10:00:00")),
            buildDealWith(AFKS, 2L, LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith(AFKS, 3L, LocalDateTime.parse("2023-12-08T10:15:00"))
        );
        initHistoryValues(
            buildTradingResultWith(AFKS, LocalDate.parse("2023-12-08")).build(),
            buildTradingResultWith(AFKS, LocalDate.parse("2023-12-09")).build(),
            buildTradingResultWith(AFKS, LocalDate.parse("2023-12-10")).build(),
            buildTradingResultWith(AFKS, LocalDate.parse("2023-12-11")).build()
        );

        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));

        assertEquals(1, getInstruments(datasourceId).size());
        assertEquals(3, getIntradayValuesBy(AFKS).size());
        assertEquals(4, getHistoryValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T9. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных инструмента AFKS.
        Текущая дата 2023-12-19T10:00:00, неторговый день, сделок нет.
        Результат: Загружены данные итогов торгов.
        """)
    void testCase9() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-19T10:00:00");
        integrateInstruments(datasourceId, afks());
        clearLogs();
        initHistoryValues(generateHistoryValues(
            AFKS,
            dateTimeProvider().monthsAgo(6),
            dateTimeProvider().daysAgo(1)
        ));

        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));

        assertEquals(
            131,
            getHistoryValuesBy(AFKS).size()
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
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T10:15:00");
        integrateInstruments(datasourceId, afks());
        clearLogs();
        initIntradayValues(
            buildDealWith(AFKS,1L, LocalDateTime.parse("2023-12-08T10:00:00")),
            buildDealWith(AFKS,2L, LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith(AFKS,2L, LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith(AFKS,2L, LocalDateTime.parse("2023-12-08T10:03:00")),
            buildDealWith(AFKS,5L, LocalDateTime.parse("2023-12-08T10:15:00"))
        );

        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));

        assertEquals(3, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T11. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        По инструменту AFKS сохранены сделки до 2023-12-07T12:00:00.
        Текущее время 2023-12-07T13:00:00. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: сохранены только новые сделки, прошедшие после 12:00:00.
        """)
    void testCas11() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-07T12:00:00");
        integrateInstruments(datasourceId, afks());
        initIntradayValues(
            buildDealWith(AFKS, 1L, LocalDateTime.parse("2023-12-07T11:00:00")),
            buildDealWith(AFKS, 2L, LocalDateTime.parse("2023-12-07T11:30:00"))
        );
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));
        clearLogs();
        initTodayDateTime("2023-12-07T13:00:00");
        initIntradayValues(
            buildDealWith(AFKS, 1L, LocalDateTime.parse("2023-12-07T11:00:00")),
            buildDealWith(AFKS, 2L, LocalDateTime.parse("2023-12-07T11:30:00")),
            buildDealWith(AFKS, 3L, LocalDateTime.parse("2023-12-07T12:10:00")),
            buildDealWith(AFKS, 4L, LocalDateTime.parse("2023-12-07T12:30:00")),
            buildDealWith(AFKS, 5L, LocalDateTime.parse("2023-12-07T12:40:00"))
        );

        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));

        assertEquals(5, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T12. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        По инструменту AFKS сохранены итоги торгов по 2023-12-07.
        Текущее время 2023-12-09T13:00:00. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: добавлена история торгов за 2023-12-08.
        """)
    void testCas12() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(datasourceId, afks());
        initHistoryValues(generateHistoryValues(AFKS, nowMinus3Month(), nowMinus1Days()));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));
        clearLogs();
        initTodayDateTime("2023-12-09T13:00:00");
        initHistoryValues(generateHistoryValues(AFKS, nowMinus3Month(), nowMinus1Days()));

        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));

        assertEquals(66, getHistoryValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T13. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Обновление инструмента AFKS не включено. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: торговые данные не загружены.
        """)
    void testCase13() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(datasourceId, afks());
        initIntradayValues(buildDealWith(AFKS,1L, LocalDateTime.parse("2023-12-07T11:00:00")));
        initHistoryValues(buildDealResultBy(AFKS, "2024-01-03", 10D, 10D, 10D, 10D));

        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));

        assertEquals(0, getHistoryValuesBy(AFKS).size());
        assertEquals(0, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T14. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Данные торгов успешно проинтегрированы, обновление инструмента включено.
        Обновление инструмента выключается и запускается интеграция торговых данных.
        Результат: новые торговые данные не загружены.
        """)
    void testCase14() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(datasourceId, afks());
        initIntradayValues(buildBuyDealBy(AFKS, 1L, "10:00:00", 10D, 10D, 1));
        initHistoryValues(buildDealResultBy(AFKS, "2023-12-07", 10D, 10D, 10D, 10D));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));
        clearLogs();
        initIntradayValues(
            buildBuyDealBy(AFKS, 1L,"10:00:00", 10D, 10D, 1),
            buildBuyDealBy(AFKS, 1L,"11:00:00", 10D, 10D, 1)
        );
        initHistoryValues(
            buildDealResultBy(AFKS, "2023-12-06", 10D, 10D, 10D, 10D),
            buildDealResultBy(AFKS, "2023-12-07", 10D, 10D, 10D, 10D)
        );

        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));

        assertEquals(1, getHistoryValuesBy(AFKS).size());
        assertEquals(1, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T15. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Попытка включить обновление по списку идентификаторов.
        Результат: ошибка, "Биржа не зарегистрирована".
        """)
    void testCase15() {
        DatasourceId datasourceId = getDatasourceId();
        commandBus().execute(new UnregisterDatasourceCommand(UUID.randomUUID(), datasourceId));
        var error = assertThrows(
            EntityNotFoundException.class,
            () -> commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS))
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
        DatasourceId datasourceId = getDatasourceId();
        commandBus().execute(new UnregisterDatasourceCommand(UUID.randomUUID(), datasourceId));
        var error = assertThrows(
            EntityNotFoundException.class,
            () -> commandBus().execute(disableUpdateInstrumentCommandFrom(datasourceId, AFKS))
        );
        assertEquals(String.format("Источник данных[id=%s] не существует.", datasourceId), error.getMessage());
    }

    @Test
    @DisplayName("""
        T17. После успешной интеграции торговых данных создается событие "Торговые данные обновлены".
        """)
    void testCase17() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        integrateInstruments(datasourceId, afks());
        initIntradayValues(buildBuyDealBy(AFKS, 1L,"10:00:00", 10D, 10D, 1));
        initHistoryValues(buildDealResultBy(AFKS, "2023-12-07", 10D, 10D, 10D, 10D));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));
        TradingDataIntegrated event = findTradingDataIntegrated().orElseThrow();
        assertEquals(datasourceId.getUuid(), event.getDatasourceId());
        assertNotNull(event.getId());
        assertEquals(dateTimeProvider().nowDateTime(), event.getCreatedAt());
    }

    @Test
    @DisplayName("""
        T18. У сделок одинаковое время, но разные номера. Все сделки сохранены.
        """)
    void testCase18() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T10:15:00");
        integrateInstruments(datasourceId, afks());
        initHistoryValues(buildDealResultBy(AFKS, "2023-12-07", 10D, 10D, 10D, 10D));
        initIntradayValues(
            buildBuyDealBy(AFKS, 1L,"10:00:00", 10D, 10D, 1),
            buildBuyDealBy(AFKS, 2L, "10:00:00", 11D, 10D, 1),
            buildBuyDealBy(AFKS, 3L, "10:00:00", 11D, 10D, 2)
        );
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));
        assertEquals(3, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T19. Одна и та же сделка интегрируется дважды. Сохранена одна сделка.
        """)
    void testCase19() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T10:15:00");
        integrateInstruments(datasourceId, afks());
        initHistoryValues(buildDealResultBy(AFKS, "2023-12-07", 10D, 10D, 10D, 10D));
        initIntradayValues(buildBuyDealBy(AFKS, 1L, "10:00:00", 10D, 10D, 1));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));
        commandBus().execute(new IntegrateTradingDataCommand(UUID.randomUUID(), datasourceId));
        assertEquals(1, getIntradayValuesBy(AFKS).size());
    }

    private Optional<TradingDataIntegrated> findTradingDataIntegrated() {
        return eventPublisher()
            .getEvents()
            .stream().filter(row -> row.getClass().equals(TradingDataIntegrated.class))
            .findFirst()
            .map(TradingDataIntegrated.class::cast);
    }
}
