package ru.ioque.investfund.adapters.unit.datasource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.adapters.datasource.HttpDatasourceProvider;
import ru.ioque.investfund.adapters.datasource.client.DatasourceRestClient;
import ru.ioque.investfund.adapters.datasource.client.dto.history.HistoryValueDto;
import ru.ioque.investfund.adapters.datasource.client.dto.instrument.CurrencyPairDto;
import ru.ioque.investfund.adapters.datasource.client.dto.instrument.FuturesDto;
import ru.ioque.investfund.adapters.datasource.client.dto.instrument.IndexDto;
import ru.ioque.investfund.adapters.datasource.client.dto.instrument.InstrumentDto;
import ru.ioque.investfund.adapters.datasource.client.dto.instrument.StockDto;
import ru.ioque.investfund.adapters.datasource.client.dto.intraday.ContractDto;
import ru.ioque.investfund.adapters.datasource.client.dto.intraday.DealDto;
import ru.ioque.investfund.adapters.datasource.client.dto.intraday.DeltaDto;
import ru.ioque.investfund.adapters.datasource.client.dto.intraday.IntradayValueDto;
import ru.ioque.investfund.adapters.uuid.RandomUUIDGenerator;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.datasource.entity.CurrencyPair;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Futures;
import ru.ioque.investfund.domain.datasource.entity.Index;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.Stock;
import ru.ioque.investfund.domain.datasource.value.Contract;
import ru.ioque.investfund.domain.datasource.value.Deal;
import ru.ioque.investfund.domain.datasource.value.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.InstrumentBatch;
import ru.ioque.investfund.domain.datasource.value.IntradayBatch;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpDatasourceProviderTest {
    private static final UUID DATASOURCE_ID = UUID.randomUUID();
    private static final String DATASOURCE_URL = "http://url.com";
    private static final UUID INSTRUMENT_ID = UUID.randomUUID();
    private static final String INSTRUMENT_TICKER = "AFKS";

    DatasourceRestClient datasourceRestClient = mock(DatasourceRestClient.class);
    DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);
    HttpDatasourceProvider datasourceProvider;

    public HttpDatasourceProviderTest() {
        this.datasourceProvider = new HttpDatasourceProvider(
            datasourceRestClient,
            new RandomUUIDGenerator(),
            dateTimeProvider
        );
    }

    @Test
    @DisplayName("""
        T1. Получение списка финансовых инструментов.
        """)
    void testCase1() {
        StockDto stockDto = StockDto.builder()
            .ticker("AFKS")
            .name("AFKS Stock Common")
            .shortName("AFKS Stock")
            .lotSize(100)
            .isin("102")
            .regNumber("102")
            .listLevel(1)
            .build();
        FuturesDto futuresDto = FuturesDto.builder()
            .ticker("BRF4")
            .name("BR FUTURES")
            .shortName("BR FUTURES")
            .lotVolume(1)
            .initialMargin(100D)
            .highLimit(100D)
            .lowLimit(100D)
            .assetCode("BR")
            .build();
        CurrencyPairDto currencyPairDto = CurrencyPairDto.builder()
            .ticker("USD_RUB")
            .name("USD RUB Pair")
            .shortName("USD RUB Pair")
            .lotSize(100)
            .faceUnit("RUB")
            .build();
        IndexDto indexDto = IndexDto.builder()
            .ticker("STOC")
            .name("Stock Common")
            .shortName("Stock")
            .annualHigh(100D)
            .annualLow(100D)
            .build();

        final List<InstrumentDto> instruments = List.of(
            stockDto, futuresDto, currencyPairDto, indexDto
        );

        when(datasourceRestClient.fetchInstruments(DATASOURCE_URL)).thenReturn(instruments);

        InstrumentBatch batch = datasourceProvider.fetchInstruments(datasource());

        assertEquals(4, batch.getUniqueValues().size());
        assertEqualsStock(stockDto, getInstrumentFromBatchByTicker(batch, stockDto.getTicker()));
        assertEqualsCurrencyPair(currencyPairDto, getInstrumentFromBatchByTicker(batch, currencyPairDto.getTicker()));
        assertEqualsIndex(indexDto, getInstrumentFromBatchByTicker(batch, indexDto.getTicker()));
        assertEqualsFutures(futuresDto, getInstrumentFromBatchByTicker(batch, futuresDto.getTicker()));
    }

    @Test
    @DisplayName("""
        T2. Получение истории инструмента.
        """)
    void testCase2() {
        final LocalDate from = LocalDate.of(2024, 1, 10);
        final LocalDate to = LocalDate.of(2024, 1, 20);
        final HistoryValueDto historyDto = HistoryValueDto.builder()
            .ticker(INSTRUMENT_TICKER)
            .tradeDate(LocalDate.of(2024, 1, 10))
            .closePrice(1D)
            .openPrice(1D)
            .highPrice(1D)
            .lowPrice(1D)
            .value(1D)
            .waPrice(1D)
            .build();

        when(datasourceRestClient.fetchHistory(DATASOURCE_URL, INSTRUMENT_TICKER, from, to)).thenReturn(List.of(historyDto));
        when(dateTimeProvider.nowDate()).thenReturn(to.plusDays(1));

        HistoryBatch batch = datasourceProvider.fetchHistoryBy(datasource(), instrument());
        assertEquals(1, batch.getUniqueValues().size());
        assertHistory(historyDto, batch.getUniqueValues().get(0));
    }

    @Test
    @DisplayName("""
        T3. Получение сделок по инструменту.
        """)
    void testCase3() {
        DealDto dealDto = DealDto.builder()
            .ticker("AFKS")
            .dateTime(LocalDateTime.parse("2024-01-10T10:00:00"))
            .isBuy(true)
            .number(1L)
            .price(10D)
            .value(10D)
            .qnt(1)
            .build();
        when(datasourceRestClient.fetchIntradayValues(DATASOURCE_URL, INSTRUMENT_TICKER, 0L)).thenReturn(List.of(dealDto));

        IntradayBatch batch = datasourceProvider.fetchIntradayValuesBy(datasource(), instrument());

        assertEquals(1, batch.getUniqueValues().size());
        assertDeal(dealDto, getIntradayFromBatchByTicker(batch, dealDto.getTicker()));
    }

    @Test
    @DisplayName("""
        T4. Получение контрактов по инструменту.
        """)
    void testCase4() {
        ContractDto contractDto = ContractDto.builder()
            .ticker(INSTRUMENT_TICKER)
            .dateTime(LocalDateTime.parse("2024-01-10T10:00:00"))
            .number(1L)
            .price(10D)
            .value(10D)
            .qnt(1)
            .build();
        when(datasourceRestClient.fetchIntradayValues(DATASOURCE_URL, INSTRUMENT_TICKER, 0L)).thenReturn(List.of(contractDto));

        IntradayBatch batch = datasourceProvider.fetchIntradayValuesBy(datasource(), instrument());

        assertEquals(1, batch.getUniqueValues().size());
        assertContract(contractDto, getIntradayFromBatchByTicker(batch, contractDto.getTicker()));
    }

    @Test
    @DisplayName("""
        T5. Получение дельты по инструменту.
        """)
    void testCase5() {
        DeltaDto indexDto = DeltaDto.builder()
            .ticker(INSTRUMENT_TICKER)
            .dateTime(LocalDateTime.parse("2024-01-10T10:00:00"))
            .number(1L)
            .price(10D)
            .value(10D)
            .build();
        when(datasourceRestClient.fetchIntradayValues(DATASOURCE_URL, INSTRUMENT_TICKER, 0L)).thenReturn(List.of(indexDto));

        IntradayBatch batch = datasourceProvider.fetchIntradayValuesBy(datasource(), instrument());

        assertEquals(1, batch.getUniqueValues().size());
        asserIntraday(indexDto, getIntradayFromBatchByTicker(batch, indexDto.getTicker()));
    }

    private void assertContract(ContractDto contractDto, IntradayValue intraday) {
        Contract contract = (Contract) intraday;
        asserIntraday(contractDto, intraday);
        assertEquals(contractDto.getQnt(), contract.getQnt());
    }


    private void assertDeal(DealDto dealDto, IntradayValue intraday) {
        Deal deal = (Deal) intraday;
        asserIntraday(dealDto, intraday);
        assertEquals(dealDto.getQnt(), deal.getQnt());
        assertEquals(dealDto.getIsBuy(), deal.getIsBuy());
    }

    private void asserIntraday(IntradayValueDto intradayValueDto, IntradayValue intradayValue) {
        assertEquals(intradayValueDto.getTicker(), intradayValue.getTicker());
        assertEquals(intradayValueDto.getValue(), intradayValue.getValue());
        assertEquals(intradayValueDto.getPrice(), intradayValue.getPrice());
        assertEquals(intradayValueDto.getNumber(), intradayValue.getNumber());
        assertEquals(intradayValueDto.getDateTime(), intradayValue.getDateTime());
    }

    private Datasource datasource() {
        return Datasource.builder()
            .id(DATASOURCE_ID)
            .name("Datasource")
            .url("http://url.com")
            .description("desc")
            .instruments(List.of(instrument()))
            .build();
    }

    private Instrument instrument() {
        return Stock.builder()
            .id(INSTRUMENT_ID)
            .ticker(INSTRUMENT_TICKER)
            .datasourceId(DATASOURCE_ID)
            .name("name")
            .shortName("name")
            .lotSize(100)
            .lastHistoryDate(LocalDate.of(2024, 1, 9))
            .build();
    }

    private IntradayValue getIntradayFromBatchByTicker(IntradayBatch batch, String ticker) {
        return batch
            .getUniqueValues()
            .stream()
            .filter(row -> row.getTicker().equals(ticker))
            .findFirst()
            .orElseThrow();
    }

    private Instrument getInstrumentFromBatchByTicker(InstrumentBatch batch, String ticker) {
        return batch
            .getUniqueValues()
            .stream()
            .filter(row -> row.getTicker().equals(ticker))
            .findFirst()
            .orElseThrow();
    }

    private void assertEqualsFutures(FuturesDto futuresDto, Instrument instrument) {
        Futures futures = (Futures) instrument;
        assertInstrument(futuresDto, futures);
        assertEquals(futuresDto.getAssetCode(), futures.getAssetCode());
        assertEquals(futuresDto.getHighLimit(), futures.getHighLimit());
        assertEquals(futuresDto.getLowLimit(), futures.getLowLimit());
        assertEquals(futuresDto.getLotVolume(), futures.getLotVolume());
        assertEquals(futuresDto.getInitialMargin(), futures.getInitialMargin());
    }

    private void assertEqualsIndex(IndexDto indexDto, Instrument instrument) {
        Index index = (Index) instrument;
        assertInstrument(indexDto, index);
        assertEquals(indexDto.getAnnualHigh(), index.getAnnualHigh());
        assertEquals(indexDto.getAnnualLow(), index.getAnnualLow());
    }

    private void assertEqualsCurrencyPair(CurrencyPairDto currencyPairDto, Instrument instrument) {
        CurrencyPair currencyPair = (CurrencyPair) instrument;
        assertInstrument(currencyPairDto, currencyPair);
        assertEquals(currencyPairDto.getFaceUnit(), currencyPair.getFaceUnit());
        assertEquals(currencyPairDto.getLotSize(), currencyPair.getLotSize());
    }

    private void assertEqualsStock(StockDto stockDto, Instrument instrument) {
        Stock stock = (Stock) instrument;
        assertInstrument(stockDto, stock);
        assertEquals(stockDto.getIsin(), stock.getIsin());
        assertEquals(stockDto.getListLevel(), stock.getListLevel());
        assertEquals(stockDto.getRegNumber(), stock.getRegNumber());
        assertEquals(stockDto.getLotSize(), stock.getLotSize());
    }

    private void assertInstrument(InstrumentDto instrumentDto, Instrument instrument) {
        assertEquals(instrumentDto.getTicker(), instrument.getTicker());
        assertEquals(instrumentDto.getName(), instrument.getName());
        assertEquals(instrumentDto.getShortName(), instrument.getShortName());
    }

    private void assertHistory(HistoryValueDto historyDto, HistoryValue historyValue) {
        assertEquals(historyDto.getTicker(), historyValue.getTicker());
        assertEquals(historyDto.getTradeDate(), historyValue.getTradeDate());
        assertEquals(historyDto.getClosePrice(), historyValue.getClosePrice());
        assertEquals(historyDto.getOpenPrice(), historyValue.getOpenPrice());
        assertEquals(historyDto.getLowPrice(), historyValue.getLowPrice());
        assertEquals(historyDto.getHighPrice(), historyValue.getHighPrice());
        assertEquals(historyDto.getWaPrice(), historyValue.getWaPrice());
        assertEquals(historyDto.getValue(), historyValue.getValue());
    }
}
