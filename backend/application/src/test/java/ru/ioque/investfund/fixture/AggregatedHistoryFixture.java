package ru.ioque.investfund.fixture;

import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;

import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

public class AggregatedHistoryFixture {

    public AggregatedTotals imoexHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return DataFactory.factoryAggregatedHistory(IMOEX, tradeDate, openPrice, closePrice, value);
    }

    public AggregatedTotals afksHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(AFKS, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedTotals tgknHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(TGKN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedTotals tgkbHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(TGKB, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedTotals tatnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(TATN, tradeDate, openPrice, closePrice, waPrice, value);
    }


    public AggregatedTotals brf4HistoryValue(String tradeDate, Double openPrice, Double closePrice,
        Double value) {
        return DataFactory.factoryAggregatedHistory(BRF4, tradeDate, openPrice, closePrice, value);
    }

    public AggregatedTotals lkohHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(LKOH, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedTotals sibnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(SIBN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedTotals rosnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(ROSN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedTotals sberHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(SBER, tradeDate, openPrice, closePrice, waPrice, value);
    }


    public AggregatedTotals sberpHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(SBERP, tradeDate, openPrice, closePrice, waPrice, value);
    }
}
