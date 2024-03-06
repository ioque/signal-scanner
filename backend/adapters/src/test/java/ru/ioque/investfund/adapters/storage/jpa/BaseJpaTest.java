package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.storage.jpa.repositories.DailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ExchangeEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.Deal;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;
import ru.ioque.investfund.domain.schedule.ScheduleUnit;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@SpringBootTest
@Transactional
@Import(JpaTestConfiguration.class)
public class BaseJpaTest {
    @Autowired
    protected UUIDProvider uuidProvider;
    @Autowired
    protected ExchangeRepository exchangeRepository;
    @Autowired
    protected ExchangeEntityRepository exchangeEntityRepository;
    @Autowired
    protected InstrumentEntityRepository instrumentEntityRepository;
    @Autowired
    protected DailyValueEntityRepository dailyValueEntityRepository;
    @Autowired
    protected IntradayValueEntityRepository intradayValueEntityRepository;

    @BeforeEach
    void beforeEach() {
        exchangeEntityRepository.deleteAll();
        instrumentEntityRepository.deleteAll();
        intradayValueEntityRepository.deleteAll();
        dailyValueEntityRepository.deleteAll();
    }

    protected Stock.StockBuilder buildStockWith() {
        return Stock
            .builder()
            .id(uuidProvider.generate())
            .ticker("AFKS")
            .shortName("ао Система")
            .name("fasfasfasfasf")
            .lotSize(1000)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1);
    }

    protected Index.IndexBuilder buildIndexWith() {
        return Index
            .builder()
            .id(uuidProvider.generate())
            .ticker("INDEX")
            .shortName("Какой-то индекс")
            .name("Какой-то индекс");
    }

    protected IntradayValue buildDealWith(String ticker, LocalDateTime dateTime) {
        return Deal.builder()
            .number(1L)
            .dateTime(dateTime)
            .ticker(ticker)
            .value(1.0)
            .price(1.0)
            .qnt(1)
            .isBuy(true)
            .build();
    }

    protected DealResult buildTradingResult(String ticker, LocalDate tradeDate) {
        return DealResult.builder()
            .tradeDate(tradeDate)
            .ticker(ticker)
            .closePrice(1.0)
            .maxPrice(1.0)
            .minPrice(1.0)
            .openPrice(1.0)
            .value(1.0)
            .numTrades(1D)
            .build();
    }

    protected ScheduleUnit.ScheduleUnitBuilder buildScheduleUnit() {
        return ScheduleUnit
            .builder()
            .id(uuidProvider.generate())
            .priority(1)
            .systemModule(SystemModule.EXCHANGE)
            .startTime(LocalTime.parse("00:00"))
            .stopTime(LocalTime.parse("23:59"));
    }
}