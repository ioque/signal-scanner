package ru.ioque.moexdatasource.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstrumentServiceTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Выгрузить список инструментов с основных режимов торгов по акциям, фьючерсам и валютным парам, а также
        индексы фондового рынка Московской Биржи.
        """)
    void testCase1() {
        getInstrumentService().downloadInstruments();

        assertEquals(892, getInstrumentService().getInstruments().size());
    }

    @Test
    @DisplayName("""
        T2. Запросить список финансовых инструментов без предварительной выгрузки
        """)
    void testCase2() {
        assertEquals(0, getInstrumentService().getInstruments().size());
    }
}
