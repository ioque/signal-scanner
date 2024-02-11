package ru.ioque.acceptance.api.datascanner.testcases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.application.datasource.DatasetManager;
import ru.ioque.acceptance.client.exchange.ExchangeRestClient;
import ru.ioque.acceptance.client.signalscanner.SignalScannerRestClient;

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
}
