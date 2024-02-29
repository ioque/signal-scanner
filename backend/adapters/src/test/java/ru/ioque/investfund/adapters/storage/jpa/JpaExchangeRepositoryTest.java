package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.domain.exchange.entity.Exchange;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EXCHANGE REPOSITORY TEST")
public class JpaExchangeRepositoryTest extends BaseJpaTest {
    ExchangeRepository exchangeRepository;

    public JpaExchangeRepositoryTest(
        @Autowired ExchangeRepository exchangeRepository
    ) {
        this.exchangeRepository = exchangeRepository;
    }

    @Test
    @DisplayName("Т1. Сохранение данных о бирже без инструментов")
    void testCase1() {
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            Set.of(),
            List.of()
        );
        exchangeRepository.save(exchange);

        assertTrue(exchangeRepository.getBy(LocalDate.now()).isPresent());
        assertEquals(exchange, exchangeRepository.getBy(LocalDate.now()).get());
    }

    @Test
    @DisplayName("Т2. Сохранение данных о бирже с инструментами")
    void testCase2() {
        var instrument = buildStockWith().build();
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            Set.of(instrument.getId()),
            List.of(
                instrument
            )
        );
        exchangeRepository.save(exchange);

        assertTrue(exchangeRepository.getBy(LocalDate.now()).isPresent());
        assertEquals(1, exchangeRepository.getBy(LocalDate.now()).get().getInstruments().size());
        assertEquals(instrument, exchangeRepository.getBy(LocalDate.now()).get().getInstruments().get(0));
    }

    @Test
    @DisplayName("Т3. Обновление торговых данных.")
    void testCase3() {
        var instrument = buildStockWith().build();
        var dailyTradingResult = buildTradingResult(instrument.getTicker(), LocalDate.now().minusDays(1));
        var intradayValue = buildDealWith(instrument.getTicker(), LocalDate.now().atTime(LocalTime.parse("10:00:00")));
        instrument.addDailyValue(dailyTradingResult);
        instrument.addIntradayValue(intradayValue);
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            Set.of(instrument.getId()),
            List.of(
                instrument
            )
        );

        exchangeRepository.save(exchange);

        assertTrue(exchangeRepository.getBy(LocalDate.now()).isPresent());
        assertEquals(1, exchangeRepository.getBy(LocalDate.now()).get().getInstruments().size());
        assertEquals(instrument, exchangeRepository.getBy(LocalDate.now()).get().getInstruments().get(0));
        assertEquals(1, exchangeRepository.getBy(LocalDate.now()).get().getInstruments().get(0).getDailyValues().size());
        assertEquals(1, exchangeRepository.getBy(LocalDate.now()).get().getInstruments().get(0).getIntradayValues().size());
        assertEquals(dailyTradingResult, exchangeRepository.getBy(LocalDate.now()).get().getInstruments().get(0).getDailyValues().first());
        assertEquals(intradayValue, exchangeRepository.getBy(LocalDate.now()).get().getInstruments().get(0).getIntradayValues().first());
    }
}
