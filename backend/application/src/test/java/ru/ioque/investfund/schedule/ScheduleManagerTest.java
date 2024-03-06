package ru.ioque.investfund.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.schedule.ScheduleCommand;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.scanner.financial.algorithms.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCHEDULE MANAGER")
public class ScheduleManagerTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        exchangeManager().integrateWithDataSource();
        loggerProvider().clearLogs();
        scheduleManager().saveScheduleUnit(
            new ScheduleCommand(
                SystemModule.EXCHANGE,
                LocalTime.parse("00:00"),
                LocalTime.parse("23:59"),
                1
            )
        );
        scheduleManager().saveScheduleUnit(
            new ScheduleCommand(
                SystemModule.SIGNAL_SCANNER,
                LocalTime.parse("00:00"),
                LocalTime.parse("23:59"),
                2)
        );
    }

    @Test
    @DisplayName("""
        T1. В системе зарегистрирован сканер сигналов для NAUK_positive.
        В расписание добавлена задача по регулярному сканированию финансовых инструментов.
        Текущее время t1 входит в интервал рабочего времени сканера сигналов для NAUK_positive.
        В хранилище финансовых инструментов по тикеру NAUK_positive нет исторических данных
        и нет текущих сделок.
        """)
    void testCase1() {
        initTodayDateTime("2023-03-03T12:00");

        scheduleManager().executeSchedule();
        assertFalse(getInstruments().isEmpty());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(1.5, 180, "IMOEX"),
            getInstruments().stream().map(Instrument::getId).toList()
        );

        assertFalse(fakeDataScannerStorage().getAll().isEmpty());

        var exception = assertThrows(DomainException.class, () -> scheduleManager().executeSchedule());
        assertEquals("Нет статистических данных для выбранных инструментов.", exception.getMessage());
    }

    @Test
    @DisplayName("""
        T2. В системе зарегистрирован сканер сигналов для NAUK.
        В расписание добавлена задача по обновлению данных инструмента.
        """)
    void testCase2() {
        initTradingDatas();
        final var tickers = List.of("NAUK");
        initTodayDateTime("2023-12-12T12:00");
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        scheduleManager().executeSchedule();
        var instrument = getInstruments()
            .stream()
            .filter(row -> row.getTicker().equals("NAUK"))
            .findFirst()
            .orElseThrow();
        assertEquals(7, instrument.getIntradayValues().size());
        assertEquals(7, instrument.getDailyValues().size());
    }

    @Test
    @DisplayName("""
        Т4. В расписание добавлена задача по обновлению данных инструмента.
        Задача выполняется два раза за день.
        """)
    void testCase4() {
        final var tickers = List.of("NAUK");
        initTodayDateTime("2023-12-12T12:00");
        initTradingDatas();
        initTodayDateTime("2023-12-12T12:00");
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        scheduleManager().executeSchedule();
        var instrument = getInstruments()
            .stream()
            .filter(row -> row.getTicker().equals("NAUK"))
            .findFirst()
            .orElseThrow();
        assertEquals(7, instrument.getIntradayValues().size());
        assertEquals(7, instrument.getDailyValues().size());

        loggerProvider().clearLogs();

        initTodayDateTime("2023-12-12T15:00");

        scheduleManager().executeSchedule();
        instrument = getInstruments().stream().filter(row -> row.getTicker().equals("NAUK")).findFirst().orElseThrow();
        assertEquals(7, instrument.getIntradayValues().size());
        assertEquals(7, instrument.getDailyValues().size());
        assertTrue(loggerProvider()
            .logContainsMessageParts(
                "Начато выполнение актуальных задач расписания. Текущее время - 15:00, текущая дата - 2023-12-12.",
                "Начато обновление торговых данных инструмента NAUK, 2023-12-12T15:00. Текущее количество сделок 7, интервал сделок: 2023-12-12T10:11 - 2023-12-12T14:04. Текущий период исторических данных: 2023-12-08 - 2023-12-14.",
                "Завершено обновление торговых данных инструмента NAUK, 2023-12-12T15:00. Текущее количество сделок 7, интервал сделок: 2023-12-12T10:11 - 2023-12-12T14:04. Текущий период исторических данных: 2023-12-08 - 2023-12-14.",
                "Завершено выполнение актуальных задач расписания. Время начала выполнения задач - 15:00. Текущее время - 15:00, текущая дата - 2023-12-12. Время выполнения < 1 мс."
            )
        );
    }

    private void initTradingDatas() {
        exchangeDataFixture().initTradingResults(
            List.of(
                buildTradingResultWith("NAUK", LocalDate.parse("2023-12-08")).build(),
                buildTradingResultWith("NAUK", LocalDate.parse("2023-12-09")).build(),
                buildTradingResultWith("NAUK", LocalDate.parse("2023-12-10")).build(),
                buildTradingResultWith("NAUK", LocalDate.parse("2023-12-11")).build(),
                buildTradingResultWith("NAUK", LocalDate.parse("2023-12-12")).build(),
                buildTradingResultWith("NAUK", LocalDate.parse("2023-12-13")).build(),
                buildTradingResultWith("NAUK", LocalDate.parse("2023-12-14")).build(),
                buildDeltaResultBy("IMOEX", "2023-12-13", 2980D, 2980D, 10D),
                buildDeltaResultBy("IMOEX", "2023-12-14", 2980D, 2990D, 10D)
            )
        );
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-12T10:11:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-12T10:23:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-12T11:02:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-12T11:04:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-12T12:07:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-12T13:04:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-12T14:04:00")),
                buildDeltaBy(1L, "IMOEX", "10:00:00", 3000D, 100D),
                buildDeltaBy(2L, "IMOEX", "11:00:00", 3002D, 200D)
            )
        );
    }

    @Test
    @DisplayName("""
        Т5. В расписание добавлена задача по обновлению данных инструмента.
        Задача выполняется в первый день.
        Задача выполняется во второй день.
        """)
    void testCase5() {
        final var tickers = List.of("NAUK");
        initTodayDateTime("2023-12-12T11:00");
        initTradingDatas();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        scheduleManager().executeSchedule();
        var instrument = getInstruments()
            .stream()
            .filter(row -> row.getTicker().equals("NAUK"))
            .findFirst()
            .orElseThrow();
        assertEquals(7, instrument.getDailyValues().size());
        initTodayDateTime("2023-12-13T11:00");
        scheduleManager().executeSchedule();
        assertEquals(7, instrument.getDailyValues().size());
        assertEquals(
            LocalDate.parse("2023-12-14"),
            instrument
                .getDailyValues()
                .stream()
                .max(Comparator.comparing(DailyValue::getTradeDate))
                .orElseThrow()
                .getTradeDate()
        );
    }

    @Test
    @DisplayName("""
        T6. В хранилище финансовых инструментов не пусто, есть данные по тикеру NAUK.
        В расписание добавляется задача на обновление ежедневной информации по NAUK.
        Интервал выполнения задачи 2 часа.
        """)
    void testCase6() {
        initTodayDateTime("2023-12-01T10:15:00");
        final var tickers = List.of("NAUK");
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-01T10:01:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-01T10:13:00"))
            )
        );
        exchangeDataFixture().initTradingResults(
            List.of(
                buildTradingResultWith("NAUK", LocalDate.parse("2023-11-29")).build(),
                buildTradingResultWith("NAUK", LocalDate.parse("2023-11-30")).build()
            )
        );
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        scheduleManager().executeSchedule();
        assertTrue(loggerProvider()
            .logContainsMessageParts(
                "Расписание модуля EXCHANGE сохранено.",
                "Начато выполнение актуальных задач расписания. Текущее время - 10:15, текущая дата - 2023-12-01.",
                "Начато обновление торговых данных инструмента NAUK, 2023-12-01T10:15. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: <_> - <_>.",
                "Завершено обновление торговых данных инструмента NAUK, 2023-12-01T10:15. Текущее количество сделок 2, интервал сделок: 2023-12-01T10:01 - 2023-12-01T10:13. Текущий период исторических данных: 2023-11-29 - 2023-11-30.",
                "Завершено выполнение актуальных задач расписания. Время начала выполнения задач - 10:15. Текущее время - 10:15, текущая дата - 2023-12-01. Время выполнения < 1 мс."
            )
        );
        loggerProvider().clearLogs();

        initTodayDateTime("2023-12-01T12:15:00");
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-01T10:01:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-01T10:13:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-01T10:15:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-01T10:24:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-01T11:36:00")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-01T11:41:40")),
                buildDealWith("NAUK", LocalDateTime.parse("2023-12-01T11:53:00"))
            )
        );
        scheduleManager().executeSchedule();
        assertTrue(loggerProvider()
            .logContainsMessageParts(
                "Начато выполнение актуальных задач расписания. Текущее время - 12:15, текущая дата - 2023-12-01.",
                "Начато обновление торговых данных инструмента NAUK, 2023-12-01T12:15. Текущее количество сделок 2, интервал сделок: 2023-12-01T10:01 - 2023-12-01T10:13. Текущий период исторических данных: 2023-11-29 - 2023-11-30.",
                "Завершено обновление торговых данных инструмента NAUK, 2023-12-01T12:15. Текущее количество сделок 7, интервал сделок: 2023-12-01T10:01 - 2023-12-01T11:53. Текущий период исторических данных: 2023-11-29 - 2023-11-30.",
                "Завершено выполнение актуальных задач расписания. Время начала выполнения задач - 12:15. Текущее время - 12:15, текущая дата - 2023-12-01. Время выполнения < 1 мс."
            )
        );
    }

    @Test
    @DisplayName("""
        T7. Обновление инструмента не должно запускаться, если задача запускалась менее 15 минут назад.
        """)
    void testCase7() {
        final var tickers = List.of("NAUK");
        initTodayDateTime("2023-12-01T10:00:00");
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        scheduleManager().executeSchedule();
        assertTrue(
            loggerProvider().logContainsMessageParts(
                "Начато выполнение актуальных задач расписания. Текущее время - 10:00, текущая дата - 2023-12-01.",
                "Начато обновление торговых данных инструмента NAUK, 2023-12-01T10:00. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: <_> - <_>.",
                "Завершено обновление торговых данных инструмента NAUK, 2023-12-01T10:00. Текущее количество сделок 0, интервал сделок: <_> - <_>. Текущий период исторических данных: <_> - <_>.",
                "Завершено выполнение актуальных задач расписания. Время начала выполнения задач - 10:00. Текущее время - 10:00, текущая дата - 2023-12-01. Время выполнения < 1 мс."
            )
        );
        loggerProvider().clearLogs();
        initTodayDateTime("2023-12-01T10:11:00");
        scheduleManager().executeSchedule();
        assertTrue(
            loggerProvider().logContainsMessageParts(
                "Начато выполнение актуальных задач расписания. Текущее время - 10:11, текущая дата - 2023-12-01.",
                "Завершено выполнение актуальных задач расписания. Время начала выполнения задач - 10:11. Текущее время - 10:11, текущая дата - 2023-12-01. Время выполнения < 1 мс."
            )
        );
        assertFalse(
            loggerProvider().logContainsMessageParts(
                "Новых сделок по инструменту NAUK нет, задача на обновление выполнена не будет.")
        );
    }
}
