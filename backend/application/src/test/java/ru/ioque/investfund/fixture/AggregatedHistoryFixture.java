package ru.ioque.investfund.fixture;

import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;

import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

public class AggregatedHistoryFixture {

    public AggregatedHistory imoexHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return DataFactory.factoryAggregatedHistory(IMOEX, tradeDate, openPrice, closePrice, value);
    }

    public AggregatedHistory afksHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(AFKS, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedHistory tgknHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(TGKN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedHistory tgkbHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(TGKB, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedHistory tatnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(TATN, tradeDate, openPrice, closePrice, waPrice, value);
    }


    public AggregatedHistory brf4HistoryValue(String tradeDate, Double openPrice, Double closePrice,
        Double value) {
        return DataFactory.factoryAggregatedHistory(BRF4, tradeDate, openPrice, closePrice, value);
    }

    public AggregatedHistory lkohHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(LKOH, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedHistory sibnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(SIBN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedHistory rosnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(ROSN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    public AggregatedHistory sberHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(SBER, tradeDate, openPrice, closePrice, waPrice, value);
    }


    public AggregatedHistory sberpHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return DataFactory.factoryAggregatedHistory(SBERP, tradeDate, openPrice, closePrice, waPrice, value);
    }
}
