package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.integration.InfrastructureTest;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.HistoryValueRepository;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Index;
import ru.ioque.investfund.domain.datasource.entity.Stock;
import ru.ioque.investfund.domain.datasource.value.Deal;
import ru.ioque.investfund.domain.datasource.value.Delta;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional
public abstract class DatabaseTest extends InfrastructureTest {
    protected static final UUID DATASOURCE_ID = UUID.randomUUID();
    protected static final UUID SCANNER_ID = UUID.randomUUID();
    @Autowired
    protected DateTimeProvider dateTimeProvider;
    @Autowired
    protected UUIDProvider uuidProvider;
    @Autowired
    protected DatasourceRepository datasourceRepository;
    @Autowired
    protected HistoryValueRepository historyValueRepository;
    @Autowired
    protected IntradayValueRepository intradayValueRepository;
    @Autowired
    protected JpaDatasourceRepository jpaDatasourceRepository;
    @Autowired
    protected JpaInstrumentRepository jpaInstrumentRepository;
    @Autowired
    protected JpaHistoryValueRepository jpaHistoryValueRepository;
    @Autowired
    protected JpaIntradayValueRepository jpaIntradayValueRepository;

    @BeforeEach
    void beforeEach() {
        jpaDatasourceRepository.deleteAll();
        jpaInstrumentRepository.deleteAll();
        jpaIntradayValueRepository.deleteAll();
        jpaHistoryValueRepository.deleteAll();
    }

    protected void prepareState() {
        dateTimeProvider.initToday(LocalDateTime.parse("2024-04-04T12:00:00"));
        datasourceRepository.save(createExchange());
        historyValueRepository.saveAll(
            List.of(
                createHistoryValue("TGKN", LocalDate.parse("2024-04-01")),
                createHistoryValue("TGKN", LocalDate.parse("2024-04-02")),
                createHistoryValue("TGKN", LocalDate.parse("2024-04-03")),
                createHistoryValue("TGKB", LocalDate.parse("2024-04-01")),
                createHistoryValue("TGKB", LocalDate.parse("2024-04-02")),
                createHistoryValue("TGKB", LocalDate.parse("2024-04-03")),
                createHistoryValue("IMOEX", LocalDate.parse("2024-04-01")),
                createHistoryValue("IMOEX", LocalDate.parse("2024-04-02")),
                createHistoryValue("IMOEX", LocalDate.parse("2024-04-03"))
            )
        );
        intradayValueRepository.saveAll(
            List.of(
                createDeal(1L, "TGKN", LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(2L, "TGKN", LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(3L, "TGKN", LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(1L, "TGKB", LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(2L, "TGKB", LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(3L, "TGKB", LocalDateTime.parse("2024-04-04T12:00:00")),
                createDelta(1L, "IMOEX", LocalDateTime.parse("2024-04-04T10:00:00")),
                createDelta(2L, "IMOEX", LocalDateTime.parse("2024-04-04T11:00:00")),
                createDelta(3L, "IMOEX", LocalDateTime.parse("2024-04-04T12:00:00"))
            )
        );
    }

    private Datasource createExchange() {
        return new Datasource(
            DATASOURCE_ID,
            "Московская биржа",
            "https://moex.com",
            "description",
            List.of(
                createImoex(), createTgkb(), createTgkn()
            )
        );
    }


    protected Stock createTgkn() {
        return Stock
            .builder()
            .id(uuidProvider.generate())
            .datasourceId(DATASOURCE_ID)
            .ticker("TGKN")
            .shortName("TGK НННН")
            .name("fasfasfasfasf")
            .lotSize(1000)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .build();
    }

    protected Stock createTgkb() {
        return Stock
            .builder()
            .id(uuidProvider.generate())
            .datasourceId(DATASOURCE_ID)
            .ticker("TGKB")
            .shortName("ТГК ББББ")
            .name("fasfasfasfasf")
            .lotSize(1000)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .build();
    }

    protected Index createImoex() {
        return Index
            .builder()
            .id(uuidProvider.generate())
            .datasourceId(DATASOURCE_ID)
            .ticker("IMOEX")
            .shortName("ао Система")
            .name("fasfasfasfasf")
            .build();
    }

    protected IntradayValue createDeal(Long number, String ticker, LocalDateTime dateTime) {
        return Deal.builder()
            .datasourceId(DATASOURCE_ID)
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .value(1.0)
            .price(1.0)
            .qnt(1)
            .isBuy(true)
            .build();
    }

    protected IntradayValue createDelta(Long number, String ticker, LocalDateTime dateTime) {
        return Delta.builder()
            .datasourceId(DATASOURCE_ID)
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .value(1.0)
            .price(1.0)
            .build();
    }

    protected HistoryValue createHistoryValue(String ticker, LocalDate tradeDate) {
        return HistoryValue.builder()
            .datasourceId(DATASOURCE_ID)
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