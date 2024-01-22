package ru.ioque.acceptance.api.datascanner.testcases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.application.datasource.DatasetManager;
import ru.ioque.acceptance.client.exchange.ExchangeRestClient;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentType;
import ru.ioque.acceptance.domain.dataemulator.stock.Stock;
import ru.ioque.acceptance.client.signalscanner.response.FinancialInstrumentInList;
import ru.ioque.acceptance.client.signalscanner.SignalScannerRestClient;
import ru.ioque.acceptance.client.signalscanner.request.AnomalyVolumeScannerConfigRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("МОДУЛЬ \"СКАНЕР ДАННЫХ\"")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class SignalScannerAcceptanceTest {
    @Autowired
    ExchangeRestClient exchangeRestClient;
    @Autowired
    SignalScannerRestClient dataScannerRestClient;
    @Autowired
    DatasetManager datasetManager;

    @BeforeEach
    void beforeEach() {
        exchangeRestClient.clear();
    }

    @Test
    @DisplayName("""
        T1. Создание сканера сигналов с алгоритмом "Аномальные объемы".
        """)
    void testCase1() {

    }

    @Test
    @DisplayName("""
        T2. Создание сканера сигналов с алгоритмом "Дельта анализ пар преф-обычка".
        """)
    void testCase2() {

    }

    @Test
    @DisplayName("""
        T3. Создание сканера сигналов с алгоритмом "Секторальный отстающий".
        """)
    void testCase3() {

    }

    @Test
    @DisplayName("""
        T4. Создание сканера сигналов с алгоритмом "Корреляция сектора с фьючерсом на товар сектора".
        """)
    void testCase4() {

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


    @Test
    @DisplayName("""
        T1. Создание сканера данных для AFKS.
        """)
    void testCase55() {
        datasetManager.initDataset(List.of(
            Stock.builder()
                .type(InstrumentType.STOCK)
                .secId("AFKS")
                .boardId("TQBR")
                .lotSize(100)
                .secName("АО СИСТЕМА")
                .shortname("ао Система")
                .build()
        ));
        exchangeRestClient.integrateWithDataSource();
        dataScannerRestClient
            .saveDataScannerConfig(
                AnomalyVolumeScannerConfigRequest.builder()
                    .ids(dataScannerRestClient
                        .getInstruments()
                        .stream()
                        .filter(row -> row.getTicker().equals("AFKS"))
                        .map(FinancialInstrumentInList::getId)
                        .toList())
                    .historyPeriod(180)
                    .startScaleCoefficient(1.5)
                    .build()
            );

        assertFalse(dataScannerRestClient.getDataScanners().isEmpty());
        var dataScannerId = dataScannerRestClient.getDataScanners().get(0).getId();
        var dataScanner = dataScannerRestClient.getDataScannerBy(dataScannerId);
        assertEquals(dataScannerId, dataScanner.getId());
        assertEquals("Аномальные объемы", dataScanner.getBusinessProcessorName());
    }
}
