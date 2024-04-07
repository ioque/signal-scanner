package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ExchangeEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.HistoryValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.entity.Exchange;
import ru.ioque.investfund.domain.datasource.entity.Index;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.Stock;
import ru.ioque.investfund.domain.datasource.value.Deal;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional
@SpringBootTest
public class BaseJpaTest {
    protected static final UUID EXCHANGE_ID = UUID.randomUUID();

    @Autowired
    protected UUIDProvider uuidProvider;
    @Autowired
    protected DatasourceRepository datasourceRepository;
    @Autowired
    protected ExchangeEntityRepository exchangeEntityRepository;
    @Autowired
    protected InstrumentEntityRepository instrumentEntityRepository;
    @Autowired
    protected HistoryValueEntityRepository historyValueEntityRepository;
    @Autowired
    protected IntradayValueEntityRepository intradayValueEntityRepository;

    @BeforeEach
    void beforeEach() {
        exchangeEntityRepository.deleteAll();
        instrumentEntityRepository.deleteAll();
        intradayValueEntityRepository.deleteAll();
        historyValueEntityRepository.deleteAll();
    }

    protected void prepareExchange(List<Instrument> instruments) {
        datasourceRepository.save(createExchange(instruments));
    }

    private Exchange createExchange(List<Instrument> instruments) {
        return new Exchange(
            EXCHANGE_ID,
            "Московская биржа",
            "https://moex.com",
            "description",
            instruments
        );
    }

    protected Stock.StockBuilder buildAfks() {
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

    protected HistoryValue buildTradingResult(String ticker, LocalDate tradeDate) {
        return HistoryValue.builder()
            .tradeDate(tradeDate)
            .ticker(ticker)
            .closePrice(1.0)
            .highPrice(1.0)
            .lowPrice(1.0)
            .openPrice(1.0)
            .value(1.0)
            .build();
    }
}