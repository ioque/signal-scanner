package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
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

    protected TradingSnapshot getImoex() {
        return getSnapshotBy(imoexId);
    }

    protected TradingSnapshot getTgkb() {
        return getSnapshotBy(tgkbId);
    }

    protected TradingSnapshot getTgkn() {
        return getSnapshotBy(tgknId);
    }

    protected TradingSnapshot getTatn() {
        return getSnapshotBy(tatnId);
    }

    protected TradingSnapshot getBrf4() {
        return getSnapshotBy(brf4Id);
    }
    protected TradingSnapshot getRosn() {
        return getSnapshotBy(rosnId);
    }
    protected TradingSnapshot getLkoh() {
        return getSnapshotBy(lkohId);
    }
    protected TradingSnapshot getSibn() {
        return getSnapshotBy(sibnId);
    }

    protected TradingSnapshot getSber() {
        return getSnapshotBy(sberId);
    }

    protected TradingSnapshot getSberp() {
        return getSnapshotBy(sberpId);
    }

    protected TradingSnapshot getSnapshotBy(InstrumentId instrumentId) {
        return tradingDataRepository().findAllBy(List.of(instrumentId)).get(0);
    }

    protected IntradayValue buildImoexDelta(Long number, String localTime, Double price, Double value) {
        return buildDeltaBy(imoexId, number,localTime, price, value);
    }

    protected IntradayValue buildTgknSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildSellDealBy(tgknId, number,localTime, price, value, qnt);
    }

    protected IntradayValue buildTgkbBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(tgkbId, number,localTime, price, value, qnt);
    }

    protected IntradayValue buildTgkbSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildSellDealBy(tgkbId, number,localTime, price, value, qnt);
    }

    protected IntradayValue buildTgknBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(tgknId, number,localTime, price, value, qnt);
    }

    protected HistoryValue buildImoexHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return buildDeltaResultBy(imoexId, tradeDate, openPrice, closePrice, value);
    }

    protected HistoryValue buildTgkbHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double waPrice, Double value) {
        return buildDealResultBy(tgkbId, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected HistoryValue buildTgknHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double waPrice, Double value) {
        return buildDealResultBy(tgknId, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayValue buildTatnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(tatnId, number,localTime, price, value, qnt);
    }

    protected HistoryValue buildTatnHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double waPrice, Double value) {
        return buildDealResultBy(tatnId, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayValue buildBrf4Contract(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildContractBy(brf4Id, number,localTime, price, value, qnt);
    }

    protected HistoryValue buildBrf4HistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return buildFuturesDealResultBy(brf4Id, tradeDate, openPrice, closePrice, value);
    }

    protected IntradayValue buildLkohBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(lkohId, number,localTime, price, value, qnt);
    }

    protected HistoryValue buildLkohHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double waPrice, Double value) {
        return buildDealResultBy(lkohId, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayValue buildSibnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(sibnId, number,localTime, price, value, qnt);
    }

    protected HistoryValue buildSibnHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double waPrice, Double value) {
        return buildDealResultBy(sibnId, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayValue buildRosnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(rosnId, number,localTime, price, value, qnt);
    }

    protected HistoryValue buildRosnHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double waPrice, Double value) {
        return buildDealResultBy(rosnId, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayValue buildSberBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(sberId, number,localTime, price, value, qnt);
    }

    protected HistoryValue buildSberHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double waPrice, Double value) {
        return buildDealResultBy(sberId, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayValue buildSberpBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(sberpId, number,localTime, price, value, qnt);
    }

    protected HistoryValue buildSberpHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double waPrice, Double value) {
        return buildDealResultBy(sberpId, tradeDate, openPrice, closePrice, waPrice, value);
    }
}
