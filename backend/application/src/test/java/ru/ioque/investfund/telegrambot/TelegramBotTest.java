package ru.ioque.investfund.telegrambot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.scanner.command.CreateScanner;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishSignal;
import ru.ioque.investfund.application.modules.telegrambot.command.Subscribe;
import ru.ioque.investfund.application.modules.telegrambot.command.Unsubscribe;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

@DisplayName("TELEGRAM BOT TEST")
public class TelegramBotTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Подписка на обновление.
        """)
    void testCase1() {
        final Subscribe command = new Subscribe(1L, "kukusuku");
        final LocalDateTime today = LocalDateTime.parse("2024-01-10T10:00:00");
        initTodayDateTime("2024-01-10T10:00:00");

        commandBus().execute(command);

        assertTrue(telegramChatRepository().findBy(command.getChatId()).isPresent());
        assertEquals(command.getChatId(), telegramChatRepository().findBy(command.getChatId()).get().getChatId());
        assertEquals(today, telegramChatRepository().findBy(command.getChatId()).get().getCreatedAt());
        assertEquals(
            "Вы успешно подписались на получение торговых сигналов.",
            telegramMessageSender().messages.get(command.getChatId()).get(0)
        );
    }

    @Test
    @DisplayName("""
        T2. Отписка от обновлений
        """)
    void testCase2() {
        commandBus().execute(new Subscribe(1L, "kukusuku"));
        telegramMessageSender().clear();
        final Unsubscribe command = new Unsubscribe(1L);

        commandBus().execute(command);

        assertTrue(telegramChatRepository().findBy(command.getChatId()).isEmpty());
        assertEquals(
            "Вы успешно отписались от получения торговых сигналов.",
            telegramMessageSender().messages.get(command.getChatId()).get(0)
        );
    }

    @Test
    @DisplayName("""
        T3. Отписка от обновлений по несуществующему чату.
        """)
    void testCase3() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commandBus().execute(new Unsubscribe(1L))
        );

        assertEquals(String.format("Чат[id=%s] не существует.", 1L), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T4. Повторная подписка на обновление.
        """)
    void testCase4() {
        commandBus().execute(new Subscribe(1L, "kukusuku"));
        commandBus().execute(new Subscribe(1L, "kukusuku"));

        assertEquals(1, telegramChatRepository().findAll().size());
    }

    @Test
    @DisplayName("""
        T5. Публикация торгового сигнала.
        """)
    void testCase5() {
        prepareState();

        commandBus().execute(new PublishSignal(
            signalJournal().findAllBy(getScannerId()).stream().findFirst().orElseThrow()
        ));

        assertEquals("""
            #TGKN
            Зафиксирован сигнал к покупке, алгоритм "Аномальные объемы"
                        
            Медиана исторических объемов: 1,400;
            Текущий объем: 6,000;
            Отношение текущего объема к медиане: 4.29;
            Тренд индекса растущий.    
            Изменение цены относительно цены закрытия предыдущего дня 12%
            """,
            telegramMessageSender().messages.get(1L).get(0));
    }

    private void prepareState() {
        initTodayDateTime("2024-04-24T13:00:00");
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        datasourceStorage().initInstrumentDetails(
            List.of(
                instrumentFixture.imoexDetails(),
                instrumentFixture.tgkbDetails(),
                instrumentFixture.tgknDetails()
            )
        );
        datasourceStorage().initDealDatas(
            List.of(
                intradayFixture.imoexDelta(1L, "10:00:00", 2800D, 100D),
                intradayFixture.imoexDelta(2L, "12:00:00", 3200D, 200D),
                intradayFixture.tgknBuyDeal(1L, "10:00:00", 111D, 5000D, 1),
                intradayFixture.tgknBuyDeal(2L, "10:03:00", 112D, 1000D, 1),
                intradayFixture.tgknSellDeal(3L, "11:00:00", 112D, 1000D, 1),
                intradayFixture.tgknBuyDeal(4L, "11:01:00", 113D, 1000D, 1),
                intradayFixture.tgknBuyDeal(5L, "11:45:00", 114.5D, 5000D, 1)
            )
        );
        datasourceStorage().initHistoryValues(
            List.of(
                historyFixture.tgknHistoryValue("2024-04-21", 99.D, 99.D, 99D, 1000D),
                historyFixture.tgknHistoryValue("2024-04-22", 99.D, 99.D, 99D, 2000D),
                historyFixture.tgknHistoryValue("2024-04-23", 100.D, 100.D, 100D, 1400D),
                historyFixture.imoexHistoryValue("2024-04-21", 2900D, 2900D, 1_000_000D),
                historyFixture.imoexHistoryValue("2024-04-22", 2900D, 2900D, 1_500_000D),
                historyFixture.imoexHistoryValue("2024-04-23", 3000D, 3000D, 2_000_000D)
            )
        );
        commandBus().execute(new SynchronizeDatasource(getDatasourceId()));
        commandBus().execute(new EnableUpdateInstruments(getDatasourceId(), getTickers(getDatasourceId())));
        commandBus().execute(
            CreateScanner.builder()
                .workPeriodInMinutes(1)
                .description("Аномальные объемы, третий эшелон.")
                .datasourceId(getDatasourceId())
                .tickers(Stream.of(TGKN, TGKB, IMOEX).map(Ticker::from).toList())
                .properties(
                    AnomalyVolumeProperties.builder()
                        .indexTicker(new Ticker(IMOEX))
                        .historyPeriod(3)
                        .scaleCoefficient(1.5)
                        .build()
                )
                .build()
        );
        runWorkPipeline(getDatasourceId());
        commandBus().execute(new Subscribe(1L, "kukusuku"));
        clearLogs();
        telegramMessageSender().clear();
    }

    protected InstrumentId getInstrumentIdBy(String ticker) {
        return getInstrumentBy(ticker).getId();
    }

    protected ScannerId getScannerId() {
        return scannerRepository().getScanners().values().stream().findFirst().orElseThrow().getId();
    }

    protected Instrument getInstrumentBy(String ticker) {
        return datasourceRepository().getInstrumentBy(Ticker.from(ticker));
    }
}
