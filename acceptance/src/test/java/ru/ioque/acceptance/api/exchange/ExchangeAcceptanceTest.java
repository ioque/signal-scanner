package ru.ioque.acceptance.api.exchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.api.BaseApiAcceptanceTest;
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
import ru.ioque.acceptance.domain.exchange.InstrumentStatistic;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("МОДУЛЬ \"АГРЕГАТОР\"")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ExchangeAcceptanceTest extends BaseApiAcceptanceTest {
    @Test
    @DisplayName("""
        T1. Интеграция с Московской Биржей.
        """)
    void testCase1() {
        integrateInstruments(
            instruments().imoex().build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().build()
        );

        Exchange exchange = getExchange();

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
        fullIntegrate();

        List<Instrument> instruments = getInstruments();
        assertEquals(4, instruments.size());
    }

    @Test
    @DisplayName("""
        T3. Получение статистики инструмента.
        """)
    void testCase3() {
        initInstrumentsWithTradingData();
        fullIntegrate();

        InstrumentStatistic instrumentStatistic = getInstrumentStatisticBy("SBER");

        assertEquals(37, Math.round(instrumentStatistic.getTodayValue()));
    }

    private void initInstrumentsWithTradingData() {
        LocalDateTime time = LocalDateTime.now().minusMinutes(5);
        datasetManager()
            .initDataset(
                List.of(
                    instruments()
                        .imoex()
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
