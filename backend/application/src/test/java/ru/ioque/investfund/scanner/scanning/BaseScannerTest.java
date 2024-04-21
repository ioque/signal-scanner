package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.datasource.integration.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.application.datasource.integration.dto.intraday.IntradayDataDto;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseScannerTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        loggerProvider().clearLogs();
    }

    protected void assertSignals(List<Signal> signals, int allSize, int openSize, int buySize, int sellSize) {
        assertEquals(allSize, signals.size());
        assertEquals(openSize, signals.stream().filter(Signal::isOpen).count());
        assertEquals(buySize, signals.stream().filter(Signal::isBuy).count());
        assertEquals(sellSize, signals.stream().filter(row -> !row.isBuy()).count());
    }

    protected List<Signal> getSignals() {
        return scannerRepository()
            .findAllBy(getDatasourceId())
            .stream()
            .map(SignalScanner::getSignals)
            .flatMap(Collection::stream)
            .toList();
    }

    protected TradingSnapshot getImoexSnapshot() {
        return getSnapshotBy(IMOEX);
    }

    protected TradingSnapshot getTgkbSnapshot() {
        return getSnapshotBy(TGKB);
    }

    protected TradingSnapshot getTgknSnapshot() {
        return getSnapshotBy(TGKN);
    }

    protected TradingSnapshot getTatnSnapshot() {
        return getSnapshotBy(TATN);
    }

    protected TradingSnapshot getBrf4Snapshot() {
        return getSnapshotBy(BRF4);
    }

    protected TradingSnapshot getRosnSnapshot() {
        return getSnapshotBy(ROSN);
    }

    protected TradingSnapshot getLkohSnapshot() {
        return getSnapshotBy(LKOH);
    }

    protected TradingSnapshot getSibnSnapshot() {
        return getSnapshotBy(SIBN);
    }

    protected TradingSnapshot getSberSnapshot() {
        return getSnapshotBy(SBER);
    }

    protected TradingSnapshot getSberpSnapshot() {
        return getSnapshotBy(SBERP);
    }

    protected TradingSnapshot getSnapshotBy(String ticker) {
        Instrument instrument = datasourceRepository()
            .findAll()
            .stream()
            .map(Datasource::getInstruments)
            .flatMap(Collection::stream)
            .filter(row -> row.getTicker().equals(new Ticker(ticker))).findFirst()
            .orElseThrow();
        return tradingDataRepository().findAllBy(List.of(instrument.getId())).get(0);
    }

    protected IntradayDataDto buildImoexDelta(Long number, String localTime, Double price, Double value) {
        return buildDeltaBy(IMOEX, number, localTime, price, value);
    }

    protected AggregatedHistoryDto buildImoexHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return buildDeltaResultBy(IMOEX, tradeDate, openPrice, closePrice, value);
    }

    protected IntradayDataDto buildTgknSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildSellDealBy(TGKN, number, localTime, price, value, qnt);
    }

    protected IntradayDataDto buildTgknBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(TGKN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildTgknHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(TGKN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildTgkbSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildSellDealBy(TGKB, number, localTime, price, value, qnt);
    }

    protected IntradayDataDto buildTgkbBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(TGKB, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildTgkbHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(TGKB, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildTatnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(TATN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildTatnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(TATN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildBrf4Contract(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildContractBy(BRF4, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildBrf4HistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return buildFuturesDealResultBy(BRF4, tradeDate, openPrice, closePrice, value);
    }

    protected IntradayDataDto buildLkohBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(LKOH, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildLkohHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(LKOH, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildSibnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(SIBN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildSibnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(SIBN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildRosnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(ROSN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildRosnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(ROSN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildSberBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(SBER, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildSberHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(SBER, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildSberpBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(SBERP, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildSberpHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(SBERP, tradeDate, openPrice, closePrice, waPrice, value);
    }
}
