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
import ru.ioque.investfund.domain.datasource.entity.indetity.InstrumentId;
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
    protected static final UUID MOEX_DATASOURCE_ID = UUID.randomUUID();
    protected static final UUID NASDAQ_DATASOURCE_ID = UUID.randomUUID();

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
                createHistoryValue(MOEX_DATASOURCE_ID, "TGKN", LocalDate.parse("2024-04-01")),
                createHistoryValue(MOEX_DATASOURCE_ID, "TGKN", LocalDate.parse("2024-04-02")),
                createHistoryValue(MOEX_DATASOURCE_ID, "TGKN", LocalDate.parse("2024-04-03")),
                createHistoryValue(MOEX_DATASOURCE_ID, "TGKB", LocalDate.parse("2024-04-01")),
                createHistoryValue(MOEX_DATASOURCE_ID, "TGKB", LocalDate.parse("2024-04-02")),
                createHistoryValue(MOEX_DATASOURCE_ID, "TGKB", LocalDate.parse("2024-04-03")),
                createHistoryValue(MOEX_DATASOURCE_ID, "IMOEX", LocalDate.parse("2024-04-01")),
                createHistoryValue(MOEX_DATASOURCE_ID, "IMOEX", LocalDate.parse("2024-04-02")),
                createHistoryValue(MOEX_DATASOURCE_ID, "IMOEX", LocalDate.parse("2024-04-03")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, "APPL", LocalDate.parse("2024-04-01")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, "APPL", LocalDate.parse("2024-04-02")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, "APPL", LocalDate.parse("2024-04-03")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, "APPLP", LocalDate.parse("2024-04-01")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, "APPLP", LocalDate.parse("2024-04-02")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, "APPLP", LocalDate.parse("2024-04-03")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, "COMP", LocalDate.parse("2024-04-01")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, "COMP", LocalDate.parse("2024-04-02")),
                createHistoryValue(NASDAQ_DATASOURCE_ID, "COMP", LocalDate.parse("2024-04-03"))
            )
        );
        intradayValueRepository.saveAll(
            List.of(
                createDeal(MOEX_DATASOURCE_ID, 1L, "TGKN", LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(MOEX_DATASOURCE_ID, 2L, "TGKN", LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(MOEX_DATASOURCE_ID, 3L, "TGKN", LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(MOEX_DATASOURCE_ID, 1L, "TGKB", LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(MOEX_DATASOURCE_ID, 2L, "TGKB", LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(MOEX_DATASOURCE_ID, 3L, "TGKB", LocalDateTime.parse("2024-04-04T12:00:00")),
                createDelta(MOEX_DATASOURCE_ID, 1L, "IMOEX", LocalDateTime.parse("2024-04-04T10:00:00")),
                createDelta(MOEX_DATASOURCE_ID, 2L, "IMOEX", LocalDateTime.parse("2024-04-04T11:00:00")),
                createDelta(MOEX_DATASOURCE_ID, 3L, "IMOEX", LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, 1L, "APPL", LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, 2L, "APPL", LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, 3L, "APPL", LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, 1L, "APPLP", LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, 2L, "APPLP", LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(NASDAQ_DATASOURCE_ID, 3L, "APPLP", LocalDateTime.parse("2024-04-04T12:00:00")),
                createDelta(NASDAQ_DATASOURCE_ID, 1L, "COMP", LocalDateTime.parse("2024-04-04T10:00:00")),
                createDelta(NASDAQ_DATASOURCE_ID, 2L, "COMP", LocalDateTime.parse("2024-04-04T11:00:00")),
                createDelta(NASDAQ_DATASOURCE_ID, 3L, "COMP", LocalDateTime.parse("2024-04-04T12:00:00"))
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
            .id(InstrumentId.of("COMP", NASDAQ_DATASOURCE_ID))
            .ticker("COMP")
            .name("NASDAQ Composite Index")
            .shortName("NASDAQ Composite Index")
            .updatable(true)
            .build();
    }

    protected Stock createApplp() {
        return Stock.builder()
            .id(InstrumentId.of("APPLP", NASDAQ_DATASOURCE_ID))
            .ticker("APPLP")
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
            .id(InstrumentId.of("APPL", NASDAQ_DATASOURCE_ID))
            .ticker("APPL")
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
            .id(InstrumentId.of("TGKN", MOEX_DATASOURCE_ID))
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
            .id(InstrumentId.of("TGKB", MOEX_DATASOURCE_ID))
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
            .id(InstrumentId.of("IMOEX", MOEX_DATASOURCE_ID))
            .ticker("IMOEX")
            .shortName("ао Система")
            .name("fasfasfasfasf")
            .build();
    }

    protected Futures createBrf4() {
        return Futures
            .builder()
            .id(InstrumentId.of("BRF4", MOEX_DATASOURCE_ID))
            .ticker("BRF4")
            .shortName("Фьючерс Брент")
            .name("Фьючерс Брент")
            .build();
    }

    protected IntradayValue createDeal(UUID datasourceId, Long number, String ticker, LocalDateTime dateTime) {
        return Deal.builder()
            .datasourceId(datasourceId)
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .value(1.0)
            .price(1.0)
            .qnt(1)
            .isBuy(true)
            .build();
    }

    protected IntradayValue createDelta(UUID datasourceId, Long number, String ticker, LocalDateTime dateTime) {
        return Delta.builder()
            .datasourceId(datasourceId)
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .value(1.0)
            .price(1.0)
            .build();
    }

    protected HistoryValue createHistoryValue(UUID datasourceId, String ticker, LocalDate tradeDate) {
        return HistoryValue.builder()
            .datasourceId(datasourceId)
            .tradeDate(tradeDate)
            .ticker(ticker)
            .waPrice(1.0)
            .closePrice(1.0)
            .highPrice(1.0)
            .lowPrice(1.0)
            .openPrice(1.0)
            .value(1.0)
            .build();
    }
}