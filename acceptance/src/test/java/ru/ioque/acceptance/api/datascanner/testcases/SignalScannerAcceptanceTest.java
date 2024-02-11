package ru.ioque.acceptance.api.datascanner.testcases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.api.exchange.fixture.InstrumentsFixture;
import ru.ioque.acceptance.application.datasource.DatasetManager;
import ru.ioque.acceptance.client.exchange.ExchangeRestClient;
import ru.ioque.acceptance.client.signalscanner.SignalScannerRestClient;
import ru.ioque.acceptance.client.signalscanner.request.AnomalyVolumeScannerRequest;
import ru.ioque.acceptance.client.signalscanner.request.CorrelationSectoralScannerRequest;
import ru.ioque.acceptance.client.signalscanner.request.PrefSimpleRequest;
import ru.ioque.acceptance.client.signalscanner.request.SectoralRetardScannerRequest;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("МОДУЛЬ \"СКАНЕР ДАННЫХ\"")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class SignalScannerAcceptanceTest {
    @Autowired
    ExchangeRestClient exchangeRestClient;
    @Autowired
    SignalScannerRestClient signalScannerRestClient;
    @Autowired
    DatasetManager datasetManager;
    InstrumentsFixture instrumentsFixture = new InstrumentsFixture();
    @BeforeEach
    void beforeEach() {
        exchangeRestClient.clear();
    }
    private InstrumentsFixture instruments() {
        return instrumentsFixture;
    }
    @Test
    @DisplayName("""
        T1. Создание сканера сигналов с алгоритмом "Аномальные объемы".
        """)
    void testCase1() {
        datasetManager.initDataset(
            List.of(
                instruments().imoex().build(),
                instruments().sber().build()
            )
        );
        exchangeRestClient.integrateWithDataSource();
        AnomalyVolumeScannerRequest request = AnomalyVolumeScannerRequest.builder()
            .scaleCoefficient(1.5)
            .description("desc")
            .historyPeriod(180)
            .indexTicker("IMOEX")
            .ids(exchangeRestClient.getExchange().getInstruments().stream().map(InstrumentInList::getId).toList())
            .build();
        signalScannerRestClient.saveDataScannerConfig(request);
        assertEquals(1, signalScannerRestClient.getDataScanners().size());
    }

    @Test
    @DisplayName("""
        T2. Создание сканера сигналов с алгоритмом "Дельта анализ пар преф-обычка".
        """)
    void testCase2() {
        datasetManager.initDataset(
            List.of(
                instruments().sberp().build(),
                instruments().sber().build()
            )
        );
        exchangeRestClient.integrateWithDataSource();

        PrefSimpleRequest request = PrefSimpleRequest.builder()
            .ids(exchangeRestClient.getExchange().getInstruments().stream().map(InstrumentInList::getId).toList())
            .description("desc")
            .spreadParam(1.0)
            .build();
        signalScannerRestClient.saveDataScannerConfig(request);
        assertEquals(1, signalScannerRestClient.getDataScanners().size());
    }

    @Test
    @DisplayName("""
        T3. Создание сканера сигналов с алгоритмом "Секторальный отстающий".
        """)
    void testCase3() {
        datasetManager.initDataset(
            List.of(
                instruments().sibn().build(),
                instruments().lkoh().build(),
                instruments().tatn().build(),
                instruments().rosn().build()
            )
        );
        exchangeRestClient.integrateWithDataSource();

        SectoralRetardScannerRequest request = SectoralRetardScannerRequest.builder()
            .ids(exchangeRestClient.getExchange().getInstruments().stream().map(InstrumentInList::getId).toList())
            .description("desc")
            .historyScale(0.015)
            .intradayScale(0.015)
            .build();
        signalScannerRestClient.saveDataScannerConfig(request);
        assertEquals(1, signalScannerRestClient.getDataScanners().size());
    }

    @Test
    @DisplayName("""
        T4. Создание сканера сигналов с алгоритмом "Корреляция сектора с фьючерсом на товар сектора".
        """)
    void testCase4() {
        datasetManager.initDataset(
            List.of(
                instruments().sibn().build(),
                instruments().lkoh().build(),
                instruments().tatn().build(),
                instruments().rosn().build(),
                instruments().brf4().build()
            )
        );
        exchangeRestClient.integrateWithDataSource();

        CorrelationSectoralScannerRequest request = CorrelationSectoralScannerRequest.builder()
            .ids(exchangeRestClient.getExchange().getInstruments().stream().map(InstrumentInList::getId).toList())
            .description("desc")
            .futuresTicker("BRF4")
            .futuresOvernightScale(0.015)
            .stockOvernightScale(0.015)
            .build();
        signalScannerRestClient.saveDataScannerConfig(request);
        assertEquals(1, signalScannerRestClient.getDataScanners().size());
    }

    @Test
    @DisplayName("""
        T5. Запуск сканера сигналов с алгоритмом "Аномальные объемы", в торговых данных есть сигнал к покупке.
        """)
    void testCase5() {

    }

    @Test
    @DisplayName("""
        T6. Запуск сканера сигналов с алгоритмом "Дельта анализ пар преф-обычка", в торговых данных есть сигнал к покупке.
        """)
    void testCase6() {

    }

    @Test
    @DisplayName("""
        T7. Запуск сканера сигналов с алгоритмом "Секторальный отстающий", в торговых данных есть сигнал к покупке.
        """)
    void testCase7() {

    }

    @Test
    @DisplayName("""
        T8. Запуск сканера сигналов с алгоритмом "Корреляция сектора с фьючерсом на товар сектора", в торговых данных есть сигнал к покупке.
        """)
    void testCase8() {

    }

    @Test
    @DisplayName("""
        T9. Запуск сканера сигналов с алгоритмом "Аномальные объемы", в торговых данных нет сигнала к покупке.
        """)
    void testCase9() {

    }

    @Test
    @DisplayName("""
        T10. Запуск сканера сигналов с алгоритмом "Дельта анализ пар преф-обычка", в торговых данных нет сигнала к покупке.
        """)
    void testCase10() {

    }

    @Test
    @DisplayName("""
        T11. Запуск сканера сигналов с алгоритмом "Секторальный отстающий", в торговых данных нет сигнала к покупке.
        """)
    void testCase11() {

    }

    @Test
    @DisplayName("""
        T12. Запуск сканера сигналов с алгоритмом "Корреляция сектора с фьючерсом на товар сектора", в торговых данных нет сигнала к покупке.
        """)
    void testCase12() {

    }
}
