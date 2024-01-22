package ru.ioque.investfund.adapters.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import ru.ioque.investfund.adapters.exchagne.moex.MoexExchangeProvider;
import ru.ioque.investfund.adapters.exchagne.moex.client.MoexRestClient;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.InstrumentDto;
import ru.ioque.investfund.adapters.exchagne.moex.client.parser.DailyTradingResultMoexParser;
import ru.ioque.investfund.adapters.exchagne.moex.client.parser.InstrumentMoexParser;
import ru.ioque.investfund.adapters.exchagne.moex.client.parser.IntradayValueMoexParser;
import ru.ioque.investfund.adapters.other.ImplDateTimeProvider;
import ru.ioque.investfund.adapters.other.RandomUUIDGenerator;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.exchange.entity.CurrencyPair;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@DisplayName("EXCHANGE PROVIDER")
public class ExchangeProviderTest {
    DateTimeProvider dateTimeProvider = new ImplDateTimeProvider();
    UUIDProvider uuidProvider = new RandomUUIDGenerator();
    MoexRestClient moexRestClient = mock(MoexRestClient.class);
    ExchangeProvider exchangeProvider = new MoexExchangeProvider(moexRestClient, dateTimeProvider, uuidProvider);


    @BeforeEach
    void beforeEach() {
        dateTimeProvider.initToday(LocalDateTime.parse("2023-04-05T12:00:00"));
    }

    @Test
    @DisplayName("""
        T1. Получение инструментов.
        """)
    void testCase1() {
        testFetchInstruments(892);
    }

    @Test
    @DisplayName("""
        T5. Получение сделок по акциям.
        """)
    void testCase5() {
        testFetchIntradayValue(imoex(), 100);
    }

    @Test
    @DisplayName("""
        T5. Получение сделок по фьючерсам.
        """)
    void testCase6() {
        testFetchIntradayValue(brf4(), 100);
    }

    @Test
    @DisplayName("""
        T5. Получение сделок по валютной паре.
        """)
    void testCase7() {
        testFetchIntradayValue(usdRub(), 100);
    }

    @Test
    @DisplayName("""
        T8. Получение изменений индекса в течение текущего дня.
        """)
    void testCase8() {
        testFetchIntradayValue(imoex(), 100);
    }

    @Test
    @DisplayName("""
        T9. Получение истории торгов акцией.
        """)
    void testCase9() {
        testFetchDailyTrading(sber(), 100);
    }

    @Test
    @DisplayName("""
        T10. Получение истории торгов фьючерсом.
        """)
    void testCase10() {
        testFetchDailyTrading(brf4(), 100);
    }

    @Test
    @DisplayName("""
        T11. Получение истории торгов валютной парой.
        """)
    void testCase11() {
        testFetchDailyTrading(usdRub(), 100);
    }

    @Test
    @DisplayName("""
        T12. Получение истории значений индекса.
        """)
    void testCase12() {
        testFetchDailyTrading(imoex(), 100);
    }

    private void testFetchInstruments(int expectedSize) {
        Mockito
            .when(moexRestClient.fetchInstruments(Stock.class))
            .thenReturn(getInstrumentsBy(Stock.class));
        Mockito
            .when(moexRestClient.fetchInstruments(Futures.class))
            .thenReturn(getInstrumentsBy(Futures.class));
        Mockito
            .when(moexRestClient.fetchInstruments(Index.class))
            .thenReturn(getInstrumentsBy(Index.class));
        Mockito
            .when(moexRestClient.fetchInstruments(CurrencyPair.class))
            .thenReturn(getInstrumentsBy(CurrencyPair.class));
        var instruments = exchangeProvider.fetchInstruments();
        assertEquals(expectedSize, instruments.size());
    }

    private void testFetchIntradayValue(Instrument instrument, int expectedSize) {
        Mockito
            .when(moexRestClient.fetchIntradayValues(instrument))
            .thenReturn(getIntradayValues(instrument));
        var intradayValues = exchangeProvider.fetchIntradayValuesBy(instrument);
        assertEquals(expectedSize, intradayValues.size());
    }

    private void testFetchDailyTrading(Instrument instrument, int expectedSize) {
        final LocalDate from = LocalDate.parse("2022-10-05");
        final LocalDate till = LocalDate.parse("2023-04-04");
        Mockito
            .when(moexRestClient.fetchDailyTradingResults(instrument, from, till))
            .thenReturn(getTradingResultsBy(instrument));
        final var tradingResults = exchangeProvider.fetchDailyTradingResultsBy(instrument);
        assertEquals(expectedSize, tradingResults.size());
    }

    private Instrument usdRub() {
        return CurrencyPair
            .builder()
            .id(UUID.randomUUID())
            .ticker("USD000UTSTOM")
            .shortName("USDRUB_TOM")
            .name("USDRUB_TOM - USD/РУБ")
            .faceUnit("RUB")
            .lotSize(1000)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    private Instrument imoex() {
        return Index
            .builder()
            .id(UUID.randomUUID())
            .ticker("IMOEX")
            .name("Индекс МосБиржи")
            .shortName("Индекс МосБиржи")
            .annualLow(100D)
            .annualHigh(100D)
            .build();
    }

    private Instrument brf4() {
        return Futures
            .builder()
            .id(UUID.randomUUID())
            .ticker("BRF4")
            .name("Фьючерсный контракт BR-1.24")
            .shortName("BR-1.24")
            .assetCode("BR")
            .lowLimit(100D)
            .highLimit(100D)
            .initialMargin(100D)
            .lotVolume(1000)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    private Instrument sber() {
        return Stock
            .builder()
            .id(UUID.randomUUID())
            .ticker("SBER")
            .shortName("Сбер")
            .name("Сбербанк")
            .lotSize(100)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    @SneakyThrows
    public List<InstrumentDto> getInstrumentsBy(Class<? extends Instrument> type) {
        Resource resource = new ClassPathResource("exchange/instruments_" + typeToFileMap.get(type) + ".json");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new InstrumentMoexParser().parse(new ObjectMapper().readTree(new String(fileData)).get("securities"), type);
    }

    @SneakyThrows
    public List<DailyValue> getTradingResultsBy(Instrument instrument) {
        Resource resource = new ClassPathResource("exchange/trading_results_" + instrument.getTicker() + ".json");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new DailyTradingResultMoexParser().parseBatch(new ObjectMapper().readTree(new String(fileData)).get("history"), instrument.getClass());
    }

    @SneakyThrows
    public List<IntradayValue> getIntradayValues(Instrument instrument) {
        Resource resource = new ClassPathResource("exchange/intraday_value_" + instrument.getTicker() + ".json");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new IntradayValueMoexParser().parseBatch(new ObjectMapper().readTree(new String(fileData)).get("trades"), instrument.getClass());
    }

    Map<Class<? extends Instrument>, String> typeToFileMap = Map.of(
        Stock.class, "STOCK",
        Futures.class, "FUTURES",
        CurrencyPair.class, "CURRENCY",
        Index.class, "INDEX"
    );
}
