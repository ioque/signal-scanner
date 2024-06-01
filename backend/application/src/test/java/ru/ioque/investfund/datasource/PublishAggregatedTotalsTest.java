package ru.ioque.investfund.datasource;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.UpdateAggregatedTotals;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.fixture.DataFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

@DisplayName("PUBLISH AGGREGATED TOTALS TEST")
public class PublishAggregatedTotalsTest extends BaseTest {
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
        T1. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов пустое.
        Выполняется создание worker'а
        """)
    void testCase1() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        initInstrumentDetails(instrumentFixture.afks());
        initAggregatedTotals(DataFactory.generateHistoryValues(AFKS, nowMinus3Month(), nowMinus1Days()));
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));

        commandBus().execute(new UpdateAggregatedTotals(datasourceId));

        assertEquals(65, getHistoryValuesBy(AFKS).size());
    }

    @Test
    @DisplayName("""
        T2. Источник биржевых данных зарегистрирован, хранилище финансовых инструментов не пустое.
        По инструменту AFKS ранее были сохранены итоги торгов по 2023-12-07.
        Текущее время 2023-12-09T13:00:00. Выполняется создание воркера.
        """)
    void testCase2() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-08T12:00:00");
        initInstrumentDetails(instrumentFixture.afks());
        initAggregatedTotals(DataFactory.generateHistoryValues(AFKS, nowMinus3Month(), nowMinus1Days()));
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(enableUpdateInstrumentCommandFrom(datasourceId, AFKS));
        commandBus().execute(new UpdateAggregatedTotals(datasourceId));
        clearLogs();
        initTodayDateTime("2023-12-09T13:00:00");
        initAggregatedTotals(DataFactory.generateHistoryValues(AFKS, nowMinus3Month(), nowMinus1Days()));

        commandBus().execute(new UpdateAggregatedTotals(datasourceId));

        assertEquals(66, getHistoryValuesBy(AFKS).size());
    }

    private LocalDate nowMinus1Days() {
        return dateTimeProvider().nowDateTime().toLocalDate().minusDays(1);
    }

    private LocalDate nowMinus3Month() {
        return dateTimeProvider().nowDateTime().minusMonths(3).toLocalDate();
    }

    private List<AggregatedTotals> getHistoryValuesBy(String ticker) {
        return aggregatedTotalsJournal().findAllBy(getInstrumentId(ticker));
    }
}
