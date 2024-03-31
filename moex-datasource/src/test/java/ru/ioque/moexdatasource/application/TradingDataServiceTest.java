package ru.ioque.moexdatasource.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradingDataServiceTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Запросить список финансовых инструментов без предварительной выгрузки
        """)
    void testCase1() {
        getInstrumentService().downloadInstruments();
        assertEquals(100, getTradingDataService().getHistory("SBER", LocalDate.now(), LocalDate.now()).size());
        assertEquals(100, getTradingDataService().getHistory("USD000UTSTOM", LocalDate.now(), LocalDate.now()).size());
        assertEquals(100, getTradingDataService().getHistory("IMOEX", LocalDate.now(), LocalDate.now()).size());
        assertEquals(100, getTradingDataService().getHistory("BRF4", LocalDate.now(), LocalDate.now()).size());
    }

    @Test
    @DisplayName("""
        T2. Запросить список финансовых инструментов без предварительной выгрузки
        """)
    void testCase2() {
        getInstrumentService().downloadInstruments();
        assertEquals(100, getTradingDataService().getIntradayValues("SBER", 0L).size());
        assertEquals(100, getTradingDataService().getIntradayValues("USD000UTSTOM", 0L).size());
        assertEquals(100, getTradingDataService().getIntradayValues("IMOEX", 0L).size());
        assertEquals(100, getTradingDataService().getIntradayValues("BRF4", 0L).size());
    }
}
