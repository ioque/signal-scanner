package ru.ioque.investfund.adapters.integration.db;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.IntradayDataJournal;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PsqlIntradayDataJournalTest extends DatabaseTest {
    @Autowired
    private IntradayDataJournal intradayDataJournal;

    @Test
    @DisplayName("""
        T1. Тестирование потока данных по журналу
        """)
    void testCase1() {
        final List<IntradayData> receiving = new ArrayList<>();
        final List<IntradayData> sending = List.of(
            buildDealWith().number(1L).timestamp(Instant.now()).build(),
            buildDealWith().number(2L).timestamp(Instant.now()).build(),
            buildDealWith().number(3L).timestamp(Instant.now()).build(),
            buildDealWith().number(4L).timestamp(Instant.now()).build()
        );
        intradayDataJournal.stream().subscribe(receiving::add);

        sending.forEach(intradayDataJournal::publish);

        assertEquals(sending, receiving);
    }

    @Test
    @DisplayName("""
        T2. Получение данных по временному окну
        """)
    void testCase2() {
        final Instant date1 = Instant.parse("2024-05-25T10:00:00.000Z");
        final Instant date2 = Instant.parse("2024-05-25T11:00:00.000Z");
        final Instant date3 = Instant.parse("2024-05-25T12:00:00.000Z");
        final Instant date4 = Instant.parse("2024-05-25T13:00:00.000Z");
        final List<IntradayData> sending = List.of(
            buildDealWith().number(1L).timestamp(date1).build(),
            buildDealWith().number(2L).timestamp(date2).build(),
            buildDealWith().number(3L).timestamp(date3).build(),
            buildDealWith().number(4L).timestamp(date4).build()
        );

        sending.forEach(intradayDataJournal::publish);

        final List<IntradayData> intradayData = intradayDataJournal.findAllBy(AFKS_ID, date2, date4);
        assertEquals(3, intradayData.size());
    }

    private Deal.DealBuilder buildDealWith() {
        return Deal.builder()
            .instrumentId(AFKS_ID)
            .ticker(AFKS)
            .value(10.0)
            .price(10.0)
            .qnt(10)
            .isBuy(true);
    }
}
