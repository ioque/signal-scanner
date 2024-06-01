package ru.ioque.investfund.datasource;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.UpdateAggregatedTotals;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.application.modules.datasource.command.RemoveDatasource;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.fixture.DataFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

@DisplayName("PUBLISH INTRADAY DATA TEST")
public class PublishIntradayDataTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
    }

    @Test
    @DisplayName("""
        T1. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных по инструменту AFKS.
        Все данные источника валидны, дублирующих записей нет.
        Результат: сохранены текущие сделки и история торгов.
        """)
    void testCase1() {
        final DatasourceId datasourceId = getDatasourceId();
        datasourceStorage().initInstrumentDetails(List.of(instrumentFixture.afks()));
        initTodayDateTime("2023-12-08T10:15:00");
        initIntradayData(
            DataFactory.factoryDealWith(AFKS,1L, LocalDateTime.parse("2023-12-08T10:00:00")),
            DataFactory.factoryDealWith(AFKS, 2L, LocalDateTime.parse("2023-12-08T10:03:00")),
            DataFactory.factoryDealWith(AFKS, 3L, LocalDateTime.parse("2023-12-08T10:15:00"))
        );
        initAggregatedTotals(
            historyFixture.afksHistoryValue("2023-12-04", 10D, 10D, 10D, 10D),
            historyFixture.afksHistoryValue("2023-12-05", 10D, 10D, 10D, 10D),
            historyFixture.afksHistoryValue("2023-12-06", 10D, 10D, 10D, 10D),
            historyFixture.afksHistoryValue("2023-12-07", 10D, 10D, 10D, 10D)
        );
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        clearLogs();

        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new UpdateAggregatedTotals(datasourceId));
        commandBus().execute(new PublishIntradayData(datasourceId));

        assertEquals(1, getInstruments(datasourceId).size());
        assertEquals(3, getIntradayValuesBy(AFKS).size());
        assertEquals(4, getHistoryValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T3. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Запущена интеграция торговых данных инструмента AFKS.
        В данных по сделкам есть дублирующие записи.
        Результат: дубликаты не сохранены, остальные сделки успешно сохранены.
        """)
    void testCase3() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T10:15:00");
        initInstrumentDetails(instrumentFixture.afks());
        clearLogs();
        initIntradayData(
            DataFactory.factoryDealWith(AFKS,1L, LocalDateTime.parse("2023-12-08T10:00:00")),
            DataFactory.factoryDealWith(AFKS,2L, LocalDateTime.parse("2023-12-08T10:03:00")),
            DataFactory.factoryDealWith(AFKS,2L, LocalDateTime.parse("2023-12-08T10:03:00")),
            DataFactory.factoryDealWith(AFKS,2L, LocalDateTime.parse("2023-12-08T10:03:00")),
            DataFactory.factoryDealWith(AFKS,5L, LocalDateTime.parse("2023-12-08T10:15:00"))
        );

        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new UpdateAggregatedTotals(datasourceId));
        commandBus().execute(new PublishIntradayData(datasourceId));

        assertEquals(3, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T4. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        По инструменту AFKS сохранены сделки до 2023-12-07T12:00:00.
        Текущее время 2023-12-07T13:00:00. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: сохранены только новые сделки, прошедшие после 12:00:00.
        """)
    void testCas4() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-07T12:00:00");
        initInstrumentDetails(instrumentFixture.afks());
        initIntradayData(
            DataFactory.factoryDealWith(AFKS, 1L, LocalDateTime.parse("2023-12-07T11:00:00")),
            DataFactory.factoryDealWith(AFKS, 2L, LocalDateTime.parse("2023-12-07T11:30:00"))
        );
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new UpdateAggregatedTotals(datasourceId));
        commandBus().execute(new PublishIntradayData(datasourceId));
        clearLogs();
        initTodayDateTime("2023-12-07T13:00:00");
        initIntradayData(
            DataFactory.factoryDealWith(AFKS, 1L, LocalDateTime.parse("2023-12-07T11:00:00")),
            DataFactory.factoryDealWith(AFKS, 2L, LocalDateTime.parse("2023-12-07T11:30:00")),
            DataFactory.factoryDealWith(AFKS, 3L, LocalDateTime.parse("2023-12-07T12:10:00")),
            DataFactory.factoryDealWith(AFKS, 4L, LocalDateTime.parse("2023-12-07T12:30:00")),
            DataFactory.factoryDealWith(AFKS, 5L, LocalDateTime.parse("2023-12-07T12:40:00"))
        );

        commandBus().execute(new PublishIntradayData(datasourceId));

        assertEquals(5, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T6. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Обновление инструмента AFKS не включено. Запущена интеграция торговых данных по инструменту AFKS.
        Результат: торговые данные не загружены.
        """)
    void testCase6() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        initInstrumentDetails(instrumentFixture.afks());
        initIntradayData(DataFactory.factoryDealWith(AFKS,1L, LocalDateTime.parse("2023-12-07T11:00:00")));
        initAggregatedTotals(historyFixture.afksHistoryValue("2024-01-03", 10D, 10D, 10D, 10D));

        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(new UpdateAggregatedTotals(datasourceId));
        commandBus().execute(new PublishIntradayData(datasourceId));

        assertEquals(0, getHistoryValuesBy(AFKS).size());
        assertEquals(0, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T7. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        Данные торгов успешно проинтегрированы, обновление инструмента включено.
        Обновление инструмента выключается и запускается интеграция торговых данных.
        Результат: новые торговые данные не загружены.
        """)
    void testCase7() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        initInstrumentDetails(instrumentFixture.afks());
        initIntradayData(intradayFixture.afksBuyDeal(1L, "10:00:00", 10D, 10D, 1));
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new UpdateAggregatedTotals(datasourceId));
        commandBus().execute(new PublishIntradayData(datasourceId));
        clearLogs();
        initIntradayData(
            intradayFixture.afksBuyDeal(1L,"10:00:00", 10D, 10D, 1),
            intradayFixture.afksBuyDeal(1L,"11:00:00", 10D, 10D, 1)
        );

        commandBus().execute(disableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new PublishIntradayData(datasourceId));

        assertEquals(1, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T8. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Попытка включить обновление по списку идентификаторов.
        Результат: ошибка, "Биржа не зарегистрирована".
        """)
    void testCase8() {
        DatasourceId datasourceId = getDatasourceId();
        commandBus().execute(new RemoveDatasource(datasourceId));
        var error = assertThrows(
            EntityNotFoundException.class,
            () -> commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS))
        );
        assertEquals(String.format("Источник данных[id=%s] не существует.", datasourceId), error.getMessage());
    }

    @Test
    @DisplayName("""
        T9. Источник биржевых данных не зарегистрирован, хранилище финансовых инструментов пустое.
        Попытка выключить обновление по списку идентификаторов.
        Результат: ошибка, "Биржа не зарегистрирована".
        """)
    void testCase9() {
        DatasourceId datasourceId = getDatasourceId();
        commandBus().execute(new RemoveDatasource(datasourceId));
        var error = assertThrows(
            EntityNotFoundException.class,
            () -> commandBus().execute(disableUpdateInstrumentCommandFrom(datasourceId, AFKS))
        );
        assertEquals(String.format("Источник данных[id=%s] не существует.", datasourceId), error.getMessage());
    }

    @Test
    @DisplayName("""
        T10. После успешной интеграции торговых данных создается событие "Торговые данные обновлены".
        """)
    void testCase10() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        initInstrumentDetails(instrumentFixture.afks());
        initIntradayData(intradayFixture.afksBuyDeal(1L,"10:00:00", 10D, 10D, 1));
        initAggregatedTotals(historyFixture.afksHistoryValue("2023-12-07", 10D, 10D, 10D, 10D));
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new PublishIntradayData(datasourceId));
    }

    @Test
    @DisplayName("""
        T11. У сделок одинаковое время, но разные номера. Все сделки сохранены.
        """)
    void testCase11() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T10:15:00");
        initInstrumentDetails(instrumentFixture.afks());
        initAggregatedTotals(historyFixture.afksHistoryValue("2023-12-07", 10D, 10D, 10D, 10D));
        initIntradayData(
            intradayFixture.afksBuyDeal(1L,"10:00:00", 10D, 10D, 1),
            intradayFixture.afksBuyDeal(2L, "10:00:00", 11D, 10D, 1),
            intradayFixture.afksBuyDeal(3L, "10:00:00", 11D, 10D, 2)
        );
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new PublishIntradayData(datasourceId));
        assertEquals(3, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T12. Одна и та же сделка интегрируется дважды. Сохранена одна сделка.
        """)
    void testCase12() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T10:15:00");
        initInstrumentDetails(instrumentFixture.afks());
        initAggregatedTotals(historyFixture.afksHistoryValue("2023-12-07", 10D, 10D, 10D, 10D));
        initIntradayData(intradayFixture.afksBuyDeal(1L, "10:00:00", 10D, 10D, 1));
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new PublishIntradayData(datasourceId));
        commandBus().execute(new PublishIntradayData(datasourceId));
        assertEquals(1, getIntradayValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T12. В команде на запуск интеграции торговых данных не передан идентификатор источника данных.
        """)
    void testCase14() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new PublishIntradayData(null))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан идентификатор источника данных.", getMessage(exception));
    }

    private List<IntradayData> getIntradayValuesBy(String ticker) {
        return intradayJournal()
            .getAll()
            .stream()
            .filter(row -> row.getTicker().equals(Ticker.from(ticker)))
            .toList();
    }

    private List<AggregatedTotals> getHistoryValuesBy(String ticker) {
        return aggregatedTotalsJournal().findAllBy(getInstrumentId(ticker));
    }
}
