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
import ru.ioque.investfund.domain.datasource.entity.Futures;
import ru.ioque.investfund.domain.datasource.entity.Index;
import ru.ioque.investfund.domain.datasource.entity.Stock;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.Deal;
import ru.ioque.investfund.domain.datasource.value.Delta;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.Ticker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional
public abstract class DatabaseTest extends InfrastructureTest {
    protected static final DatasourceId MOEX_DATASOURCE_ID = DatasourceId.from(UUID.randomUUID());
    protected static final DatasourceId NASDAQ_DATASOURCE_ID = DatasourceId.from(UUID.randomUUID());
    
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
        datasourceRepository.save(moexDatasource());
        datasourceRepository.save(nasdaqDatasource());
        historyValueRepository.saveAll(
            List.of(
                createHistoryValue(MOEX_DATASOURCE_ID, TGKN_ID, LocalDate.parse("2024-04-01")),
                createHistoryValue(MOEX_DATASOURCE_ID, TGKN_ID, LocalDate.parse("2024-04-02")),
                createHistoryValue(MOEX_DATASOURCE_ID, TGKN_ID, LocalDate.parse("2024-04-03")),
                createHistoryValue(MOEX_DATASOURCE_ID, TGKB_ID, LocalDate.parse("2024-04-01")),
                createHistoryValue(MOEX_DATASOURCE_ID, TGKB_ID, LocalDate.parse("2024-04-02")),
                createHistoryValue(MOEX_DATASOURCE_ID, TGKB_ID, LocalDate.parse("2024-04-03")),
                createHistoryValue(MOEX_DATASOURCE_ID, IMOEX_ID, LocalDate.parse("2024-04-01")),
                createHistoryValue(MOEX_DATASOURCE_ID, IMOEX_ID, LocalDate.parse("2024-04-02")),
                createHistoryValue(MOEX_DATASOURCE_ID, IMOEX_ID, LocalDate.parse("2024-04-03")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, APPL_ID, LocalDate.parse("2024-04-01")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, APPL_ID, LocalDate.parse("2024-04-02")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, APPL_ID, LocalDate.parse("2024-04-03")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, APPLP_ID, LocalDate.parse("2024-04-01")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, APPLP_ID, LocalDate.parse("2024-04-02")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, APPLP_ID, LocalDate.parse("2024-04-03")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, COMP_ID, LocalDate.parse("2024-04-01")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, COMP_ID, LocalDate.parse("2024-04-02")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, COMP_ID, LocalDate.parse("2024-04-03"))
            )
        );
        intradayValueRepository.saveAll(
            List.of(
                createDeal(MOEX_DATASOURCE_ID, TGKN_ID,1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(MOEX_DATASOURCE_ID, TGKN_ID,2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(MOEX_DATASOURCE_ID, TGKN_ID,3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(MOEX_DATASOURCE_ID, TGKB_ID,1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(MOEX_DATASOURCE_ID, TGKB_ID,2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(MOEX_DATASOURCE_ID, TGKB_ID,3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDelta(MOEX_DATASOURCE_ID, IMOEX_ID, 1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDelta(MOEX_DATASOURCE_ID, IMOEX_ID, 2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDelta(MOEX_DATASOURCE_ID, IMOEX_ID, 3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, APPL_ID, 1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, APPL_ID, 2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, APPL_ID, 3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, APPLP_ID, 1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, APPLP_ID, 2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, APPLP_ID, 3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDelta(NASDAQ_DATASOURCE_ID, COMP_ID, 1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDelta(NASDAQ_DATASOURCE_ID, COMP_ID, 2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDelta(NASDAQ_DATASOURCE_ID, COMP_ID, 3L, LocalDateTime.parse("2024-04-04T12:00:00"))
            )
        );
    }

    protected Datasource moexDatasource() {
        return new Datasource(
            MOEX_DATASOURCE_ID,
            "Московская биржа",
            "https://moex.com",
            "description",
            List.of(
                createImoex(), createTgkb(), createTgkn(), createBrf4()
            )
        );
    }

    protected Datasource nasdaqDatasource() {
        return new Datasource(
            MOEX_DATASOURCE_ID,
            "National Association of Securities Dealers Automated Quotation",
            "https://nasdaq.com",
            "National Association of Securities Dealers Automated Quotation",
            List.of(
                createAppl(), createApplp(), createComp()
            )
        );
    }

    protected Index createComp() {
        return Index.builder()
            .id(COMP_ID)
            .name("NASDAQ Composite Index")
            .shortName("NASDAQ Composite Index")
            .updatable(true)
            .build();
    }

    protected Stock createApplp() {
        return Stock.builder()
            .id(APPLP_ID)
            .name("Apple Inc. Pref Stock")
            .shortName("ApplePref")
            .lotSize(1000)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .build();
    }


    protected Stock createAppl() {
        return Stock.builder()
            .id(APPL_ID)
            .name("Apple Inc. Common Stock")
            .shortName("AppleCommon")
            .lotSize(1000)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .build();
    }


    protected Stock createTgkn() {
        return Stock
            .builder()
            .id(TGKN_ID)
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
            .id(TGKB_ID)
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
            .id(IMOEX_ID)
            .shortName("ао Система")
            .name("fasfasfasfasf")
            .build();
    }

    protected Futures createBrf4() {
        return Futures
            .builder()
            .id(BRF4_ID)
            .shortName("Фьючерс Брент")
            .name("Фьючерс Брент")
            .build();
    }

    protected IntradayValue createDeal(DatasourceId datasourceId, InstrumentId instrumentId, Long number, LocalDateTime dateTime) {
        return Deal.builder()
            .datasourceId(datasourceId)
            .instrumentId(instrumentId)
            .number(number)
            .dateTime(dateTime)
            .value(1.0)
            .price(1.0)
            .qnt(1)
            .isBuy(true)
            .build();
    }

    protected IntradayValue createDelta(DatasourceId datasourceId, InstrumentId instrumentId, Long number, LocalDateTime dateTime) {
        return Delta.builder()
            .datasourceId(datasourceId)
            .instrumentId(instrumentId)
            .number(number)
            .dateTime(dateTime)
            .value(1.0)
            .price(1.0)
            .build();
    }

    protected HistoryValue createHistoryValue(DatasourceId datasourceId, InstrumentId instrumentId, LocalDate tradeDate) {
        return HistoryValue.builder()
            .datasourceId(datasourceId)
            .instrumentId(instrumentId)
            .tradeDate(tradeDate)
            .waPrice(1.0)
            .closePrice(1.0)
            .highPrice(1.0)
            .lowPrice(1.0)
            .openPrice(1.0)
            .value(1.0)
            .build();
    }
}