package ru.ioque.acceptance.api.exchange.testcases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.api.exchange.fixture.InstrumentsFixture;
import ru.ioque.acceptance.application.datasource.DatasetManager;
import ru.ioque.acceptance.client.exchange.ExchangeRestClient;
import ru.ioque.acceptance.client.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.acceptance.client.signalscanner.SignalScannerRestClient;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairDailyResult;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairTrade;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesDailyResult;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesTrade;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDailyResult;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDelta;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;
import ru.ioque.acceptance.domain.exchange.Exchange;
import ru.ioque.acceptance.domain.exchange.Instrument;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;
import ru.ioque.acceptance.domain.exchange.InstrumentStatistic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("МОДУЛЬ \"АГРЕГАТОР\"")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ExchangeAcceptanceTest {
    @Autowired
    ExchangeRestClient exchangeRestClient;
    @Autowired
    SignalScannerRestClient dataScannerRestClient;
    @Autowired
    DatasetManager datasetManager;
    InstrumentsFixture instrumentsFixture = new InstrumentsFixture();

    @BeforeEach
    void beforeEach() {
        exchangeRestClient.clear();
    }

    @Test
    @DisplayName("""
        T1. Интеграция с Московской Биржей.
        """)
    void testCase1() {
        datasetManager.initDataset(
            List.of(
                instruments().imoex().build(),
                instruments().usbRub().build(),
                instruments().brf4().build(),
                instruments().sber().build()
            )
        );

        exchangeRestClient.integrateWithDataSource();
        Exchange exchange = exchangeRestClient.getExchange();

        assertEquals("Московская Биржа", exchange.getName());
        assertEquals(
            "Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.",
            exchange.getDescription()
        );
        assertEquals("http://localhost:8081", exchange.getUrl());
        assertEquals(4, exchange.getInstruments().size());
    }

    @Test
    @DisplayName("""
        T2. Внутридневная интеграция торговых данных.
        """)
    void testCase2() {
        initInstrumentsWithTradingData();
        exchangeRestClient.integrateWithDataSource();
        List<UUID> ids =
            exchangeRestClient.getExchange().getInstruments().stream().map(InstrumentInList::getId).toList();

        exchangeRestClient.enableUpdateInstruments(new EnableUpdateInstrumentRequest(ids));
        exchangeRestClient.integrateTradingData();

        List<Instrument> instruments = ids.stream().map(id -> exchangeRestClient.getInstrumentBy(id)).toList();
        assertEquals(4, instruments.size());
    }

    @Test
    @DisplayName("""
        T3. Получение статистики инструмента.
        """)
    void testCase3() {
        initInstrumentsWithTradingData();
        exchangeRestClient.integrateWithDataSource();
        exchangeRestClient.enableUpdateInstruments(
            new EnableUpdateInstrumentRequest(
                exchangeRestClient
                    .getInstruments()
                    .stream()
                    .map(InstrumentInList::getId)
                    .toList())
        );
        exchangeRestClient.integrateTradingData();

        InstrumentStatistic instrumentStatistic = exchangeRestClient
            .getInstrumentStatisticBy(
                exchangeRestClient
                    .getInstruments()
                    .stream()
                    .filter(row -> row.getTicker().equals("SBER"))
                    .map(InstrumentInList::getId)
                    .toList()
                    .get(0)
            );

        assertEquals(37, Math.round(instrumentStatistic.getTodayValue()));
    }

    private InstrumentsFixture instruments() {
        return instrumentsFixture;
    }

    private void initInstrumentsWithTradingData() {
        LocalDateTime time = LocalDateTime.now().minusMinutes(5);
        datasetManager.initDataset(
            List.of(
                instruments().imoex()
                    .historyValues(
                        List.of(
                            IndexDailyResult
                                .builder()
                                .close(3451.4)
                                .open(3411.1)
                                .value(135132512351.1)
                                .volume(123124124.2)
                                .secId("IMOEX")
                                .tradeDate(time.toLocalDate().minusDays(1))
                                .build()
                        )
                    )
                    .intradayValues(
                        List.of(
                            IndexDelta
                                .builder()
                                .tradeNo(1)
                                .secId("IMOEX")
                                .price(12.1)
                                .value(12.3)
                                .tradeTime(time.toLocalTime())
                                .tradeDate(time.toLocalDate())
                                .sysTime(time)
                                .build()
                        )
                    )
                    .build(),
                instruments().usbRub()
                    .historyValues(
                        List.of(
                            CurrencyPairDailyResult
                                .builder()
                                .close(3451.4)
                                .open(3411.1)
                                .volRur(135132512351.1)
                                .numTrades(1000D)
                                .secId("USD000UTSTOM")
                                .tradeDate(time.toLocalDate().minusDays(1))
                                .build()
                        )
                    )
                    .intradayValues(
                        List.of(
                            CurrencyPairTrade
                                .builder()
                                .tradeNo(1)
                                .secId("USD000UTSTOM")
                                .price(12.1)
                                .value(12.3)
                                .tradeTime(time.toLocalTime())
                                .sysTime(time)
                                .build()
                        )
                    )
                    .build(),
                instruments().brf4()
                    .historyValues(
                        List.of(
                            FuturesDailyResult
                                .builder()
                                .close(3451.4)
                                .open(3411.1)
                                .value(135132512351.1)
                                .volume(123124124)
                                .secId("BRF4")
                                .tradeDate(time.toLocalDate().minusDays(1))
                                .build()
                        )
                    )
                    .intradayValues(
                        List.of(
                            FuturesTrade
                                .builder()
                                .tradeNo(1)
                                .secId("BRF4")
                                .price(12.1)
                                .tradeTime(time.toLocalTime())
                                .tradeDate(time.toLocalDate())
                                .sysTime(time)
                                .build()
                        )
                    )
                    .build(),
                instruments().sber()
                    .historyValues(
                        List.of(
                            StockDailyResult
                                .builder()
                                .close(3451.4)
                                .open(3411.1)
                                .value(135132512351.1)
                                .volume(123124124.2)
                                .secId("SBER")
                                .tradeDate(time.toLocalDate().minusDays(1))
                                .build(),
                            StockDailyResult
                                .builder()
                                .close(3451.4)
                                .open(3411.1)
                                .value(135132512351.1)
                                .volume(123124124.2)
                                .secId("SBER")
                                .tradeDate(time.toLocalDate().minusDays(2))
                                .build(),
                            StockDailyResult
                                .builder()
                                .close(3451.4)
                                .open(3411.1)
                                .value(135132512351.1)
                                .volume(123124124.2)
                                .secId("SBER")
                                .tradeDate(time.toLocalDate().minusDays(3))
                                .build()
                        )
                    )
                    .intradayValues(
                        List.of(
                            StockTrade
                                .builder()
                                .tradeNo(1)
                                .secId("SBER")
                                .price(12.1)
                                .value(12.3)
                                .tradeTime(time.toLocalTime())
                                .sysTime(time)
                                .build(),
                            StockTrade
                                .builder()
                                .tradeNo(1)
                                .secId("SBER")
                                .price(12.1)
                                .value(12.3)
                                .tradeTime(time.plusMinutes(1).toLocalTime())
                                .sysTime(time.plusMinutes(1))
                                .build(),
                            StockTrade
                                .builder()
                                .tradeNo(1)
                                .secId("SBER")
                                .price(12.1)
                                .value(12.3)
                                .tradeTime(time.plusMinutes(2).toLocalTime())
                                .sysTime(time.plusMinutes(2))
                                .build()
                        )
                    )
                    .build()
            )
        );
    }
}
