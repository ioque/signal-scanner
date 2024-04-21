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
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetails;
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetails;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetails;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.details.StockDetails;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("HTTP DATASOURCE PROVIDER TEST")
public class HttpDatasourceProviderTest {
    private static final DatasourceId DATASOURCE_ID = DatasourceId.from(UUID.randomUUID());
    private static final String DATASOURCE_URL = "http://url.com";
    private static final String INSTRUMENT_TICKER = "AFKS";
    private static final InstrumentId INSTRUMENT_ID = InstrumentId.from(UUID.randomUUID());

    DatasourceRestClient datasourceRestClient = mock(DatasourceRestClient.class);
    DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);
    HttpDatasourceProvider datasourceProvider;

    public HttpDatasourceProviderTest() {
        this.datasourceProvider = new HttpDatasourceProvider(
            datasourceRestClient,
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

        List<InstrumentDetails> batch = datasourceProvider.fetchInstruments(datasource());

        assertEquals(4, batch.size());
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

        TreeSet<HistoryValue> batch = datasourceProvider.fetchAggregateHistory(datasource(), instrument());
        assertEquals(1, batch.size());
        assertHistory(historyDto, batch.first());
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

        TreeSet<IntradayValue> batch = datasourceProvider.fetchIntradayValues(datasource(), instrument());

        assertEquals(1, batch.size());
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

        TreeSet<IntradayValue> batch = datasourceProvider.fetchIntradayValues(datasource(), instrument());

        assertEquals(1, batch.size());
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

        TreeSet<IntradayValue> batch = datasourceProvider.fetchIntradayValues(datasource(), instrument());

        assertEquals(1, batch.size());
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
        assertEquals(intradayValueDto.getTicker(), intradayValue.getTicker().getValue());
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
        return Instrument.builder()
            .id(INSTRUMENT_ID)
            .details(
                StockDetails.builder()
                    .ticker(Ticker.from(INSTRUMENT_TICKER))
                    .name("name")
                    .shortName("name")
                    .lotSize(100)
                    .build()
            )
            .lastHistoryDate(LocalDate.of(2024, 1, 9))
            .build();
    }

    private IntradayValue getIntradayFromBatchByTicker(TreeSet<IntradayValue> batch, String ticker) {
        return batch
            .stream()
            .filter(row -> row.getTicker().getValue().equals(ticker))
            .findFirst()
            .orElseThrow();
    }

    private InstrumentDetails getInstrumentFromBatchByTicker(List<InstrumentDetails> batch, String ticker) {
        return batch
            .stream()
            .filter(row -> row.getTicker().getValue().equals(ticker))
            .findFirst()
            .orElseThrow();
    }

    private void assertEqualsFutures(FuturesDto futuresDto, InstrumentDetails instrument) {
        assertInstrument(futuresDto, instrument);
        FuturesDetails futuresDetails = (FuturesDetails) instrument;
        assertEquals(futuresDto.getAssetCode(), futuresDetails.getAssetCode());
        assertEquals(futuresDto.getHighLimit(), futuresDetails.getHighLimit());
        assertEquals(futuresDto.getLowLimit(), futuresDetails.getLowLimit());
        assertEquals(futuresDto.getLotVolume(), futuresDetails.getLotVolume());
        assertEquals(futuresDto.getInitialMargin(), futuresDetails.getInitialMargin());
    }

    private void assertEqualsIndex(IndexDto indexDto, InstrumentDetails instrument) {
        assertInstrument(indexDto, instrument);
        IndexDetails indexDetails = (IndexDetails) instrument;
        assertEquals(indexDto.getAnnualHigh(), indexDetails.getAnnualHigh());
        assertEquals(indexDto.getAnnualLow(), indexDetails.getAnnualLow());
    }

    private void assertEqualsCurrencyPair(CurrencyPairDto currencyPairDto, InstrumentDetails instrument) {
        assertInstrument(currencyPairDto, instrument);
        CurrencyPairDetails currencyPairDetails = (CurrencyPairDetails) instrument;
        assertEquals(currencyPairDto.getFaceUnit(), currencyPairDetails.getFaceUnit());
        assertEquals(currencyPairDto.getLotSize(), currencyPairDetails.getLotSize());
    }

    private void assertEqualsStock(StockDto stockDto, InstrumentDetails instrument) {
        assertInstrument(stockDto, instrument);
        StockDetails stockDetails = (StockDetails) instrument;
        assertEquals(stockDto.getIsin(), stockDetails.getIsin().getValue());
        assertEquals(stockDto.getListLevel(), stockDetails.getListLevel());
        assertEquals(stockDto.getRegNumber(), stockDetails.getRegNumber());
        assertEquals(stockDto.getLotSize(), stockDetails.getLotSize());
    }

    private void assertInstrument(InstrumentDto instrumentDto, InstrumentDetails instrument) {
        assertEquals(instrumentDto.getTicker(), instrument.getTicker().getValue());
        assertEquals(instrumentDto.getName(), instrument.getName());
        assertEquals(instrumentDto.getShortName(), instrument.getShortName());
    }

    private void assertHistory(HistoryValueDto historyDto, HistoryValue historyValue) {
        assertEquals(historyDto.getTicker(), historyValue.getTicker().getValue());
        assertEquals(historyDto.getTradeDate(), historyValue.getTradeDate());
        assertEquals(historyDto.getClosePrice(), historyValue.getClosePrice());
        assertEquals(historyDto.getOpenPrice(), historyValue.getOpenPrice());
        assertEquals(historyDto.getLowPrice(), historyValue.getLowPrice());
        assertEquals(historyDto.getHighPrice(), historyValue.getHighPrice());
        assertEquals(historyDto.getWaPrice(), historyValue.getWaPrice());
        assertEquals(historyDto.getValue(), historyValue.getValue());
    }
}
