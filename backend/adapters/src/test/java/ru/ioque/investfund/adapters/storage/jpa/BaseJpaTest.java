package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
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
    protected JpaDatasourceRepository exchangeEntityRepository;
    @Autowired
    protected JpaInstrumentRepository instrumentEntityRepository;
    @Autowired
    protected JpaHistoryValueRepository jpaHistoryValueRepository;
    @Autowired
    protected JpaIntradayValueRepository jpaIntradayValueRepository;

    @BeforeEach
    void beforeEach() {
        exchangeEntityRepository.deleteAll();
        instrumentEntityRepository.deleteAll();
        jpaIntradayValueRepository.deleteAll();
        jpaHistoryValueRepository.deleteAll();
    }

    protected void prepareExchange(List<Instrument> instruments) {
        datasourceRepository.saveDatasource(createExchange(instruments));
    }

    private Datasource createExchange(List<Instrument> instruments) {
        return new Datasource(
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