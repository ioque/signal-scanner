package ru.ioque.investfund.adapters.integration;

import org.springframework.boot.test.context.SpringBootTest;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetail;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetail;
import ru.ioque.investfund.domain.datasource.value.details.StockDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Isin;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public abstract class InfrastructureTest {
    protected static final DatasourceId MOEX_DATASOURCE_ID = DatasourceId.from(UUID.randomUUID());
    protected static final DatasourceId NASDAQ_DATASOURCE_ID = DatasourceId.from(UUID.randomUUID());
    protected static final InstrumentId COMP_ID = new InstrumentId(UUID.randomUUID());
    protected static final InstrumentId APPLP_ID = new InstrumentId(UUID.randomUUID());
    protected static final InstrumentId APPL_ID = new InstrumentId(UUID.randomUUID());
    protected static final InstrumentId AFKS_ID = new InstrumentId(UUID.randomUUID());
    protected static final InstrumentId SBER_ID = new InstrumentId(UUID.randomUUID());
    protected static final InstrumentId SBERP_ID = new InstrumentId(UUID.randomUUID());
    protected static final InstrumentId TGKN_ID = new InstrumentId(UUID.randomUUID());
    protected static final InstrumentId TGKB_ID = new InstrumentId(UUID.randomUUID());
    protected static final InstrumentId IMOEX_ID = new InstrumentId(UUID.randomUUID());
    protected static final InstrumentId BRF4_ID = new InstrumentId(UUID.randomUUID());
    protected static final Ticker COMP = new Ticker("COMP");
    protected static final Ticker APPLP = new Ticker("APPLP");
    protected static final Ticker APPL = new Ticker("APPL");
    protected static final Ticker AFKS = new Ticker("AFKS");
    protected static final Ticker SBER = new Ticker("SBER");
    protected static final Ticker SBERP = new Ticker("SBERP");
    protected static final Ticker TGKN = new Ticker("TGKN");
    protected static final Ticker TGKB = new Ticker("TGKB");
    protected static final Ticker IMOEX = new Ticker("IMOEX");
    protected static final Ticker BRF4 = new Ticker("BRF4");

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

    protected Instrument createComp() {
        return Instrument.builder()
            .id(COMP_ID)
            .detail(
                IndexDetail.builder()
                    .ticker(COMP)
                    .name("NASDAQ Composite Index")
                    .shortName("NASDAQ Composite Index")
                    .build()
            )
            .updatable(true)
            .build();
    }

    protected Instrument createApplp() {
        return Instrument.builder()
            .id(APPLP_ID)
            .detail(
                StockDetail.builder()
                    .ticker(APPLP)
                    .name("Apple Inc. Pref Stock")
                    .shortName("ApplePref")
                    .lotSize(1000)
                    .regNumber("regNumber")
                    .isin(Isin.from("ADFGDG3215"))
                    .listLevel(1)
                    .build()
            )
            .build();
    }

    protected Instrument createAppl() {
        return Instrument.builder()
            .id(APPL_ID)
            .detail(
                StockDetail.builder()
                    .ticker(APPL)
                    .name("Apple Inc. Common Stock")
                    .shortName("AppleCommon")
                    .lotSize(1000)
                    .regNumber("regNumber")
                    .isin(Isin.from("ADFGDG3215"))
                    .listLevel(1)
                    .build()
            )
            .build();
    }

    protected Instrument createAfks() {
        return Instrument
            .builder()
            .id(AFKS_ID)
            .detail(
                StockDetail.builder()
                    .ticker(AFKS)
                    .shortName("ао Система")
                    .name("АФК Система")
                    .lotSize(1000)
                    .regNumber("regNumber")
                    .isin(Isin.from("ADFGDG3215"))
                    .listLevel(1)
                    .build()
            )
            .build();
    }

    protected Instrument createSber() {
        return Instrument
            .builder()
            .id(SBER_ID)
            .detail(
                StockDetail.builder()
                    .ticker(SBER)
                    .shortName("Сбербанк")
                    .name("ПАО Сбербанк")
                    .lotSize(1000)
                    .regNumber("regNumber")
                    .isin(Isin.from("ADFGDG3215"))
                    .listLevel(1)
                    .build()
            )
            .build();
    }

    protected Instrument createSberp() {
        return Instrument
            .builder()
            .id(SBERP_ID)
            .detail(
                StockDetail.builder()
                    .ticker(SBERP)
                    .shortName("Сбербанк-п")
                    .name("ПАО Сбербанк-п")
                    .lotSize(1000)
                    .regNumber("regNumber")
                    .isin(Isin.from("ADFGDG3215"))
                    .listLevel(1)
                    .build()
            )
            .build();
    }

    protected Instrument createTgkn() {
        return Instrument
            .builder()
            .id(TGKN_ID)
            .detail(
                StockDetail.builder()
                    .ticker(TGKN)
                    .shortName("TGK НННН")
                    .name("fasfasfasfasf")
                    .lotSize(1000)
                    .regNumber("regNumber")
                    .isin(Isin.from("ADFGDG3215"))
                    .listLevel(1)
                    .build()
            )
            .build();
    }

    protected Instrument createTgkb() {
        return Instrument
            .builder()
            .id(TGKB_ID)
            .detail(
                StockDetail.builder()
                    .ticker(TGKB)
                    .shortName("ТГК ББББ")
                    .name("fasfasfasfasf")
                    .lotSize(1000)
                    .regNumber("regNumber")
                    .isin(Isin.from("ADFGDG3215"))
                    .listLevel(1)
                    .build()
            )
            .build();
    }

    protected Instrument createImoex() {
        return Instrument
            .builder()
            .id(IMOEX_ID)
            .detail(
                IndexDetail.builder()
                    .ticker(IMOEX)
                    .shortName("Индекс мосбиржи")
                    .name("Индекс мосбиржи")
                    .build()
            )
            .build();
    }

    protected Instrument createBrf4() {
        return Instrument
            .builder()
            .id(BRF4_ID)
            .detail(
                FuturesDetail.builder()
                    .ticker(BRF4)
                    .shortName("Фьючерс Брент")
                    .name("Фьючерс Брент")
                    .build()
            )
            .build();
    }

    protected IntradayData createDeal(InstrumentId instrumentId, Ticker ticker, Long number, Instant dateTime) {
        return Deal.builder()
            .instrumentId(instrumentId)
            .ticker(ticker)
            .number(number)
            .timestamp(dateTime)
            .value(1.0)
            .price(1.0)
            .qnt(1)
            .isBuy(true)
            .build();
    }

    protected AggregatedTotals createHistoryValue(LocalDate tradeDate) {
        return AggregatedTotals.builder()
            .date(tradeDate)
            .waPrice(1.0)
            .closePrice(1.0)
            .highPrice(1.0)
            .lowPrice(1.0)
            .openPrice(1.0)
            .value(1.0)
            .build();
    }
}
