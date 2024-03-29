package ru.ioque.core.tradingdatagenerator.stock;


import ru.ioque.core.dataemulator.stock.StockDailyResult;
import ru.ioque.core.tradingdatagenerator.core.HistoryGenerator;

import java.time.LocalDate;

public class StockDailyResultGenerator extends HistoryGenerator<StockDailyResult> {
    @Override
    public StockDailyResult buildHistoryValue(
        String ticker,
        LocalDate date,
        double open,
        double close,
        double value,
        double volume
    ) {
        return StockDailyResult.builder()
            .secId(ticker)
            .tradeDate(date)
            .open(open)
            .close(close)
            .value(value)
            .volume(volume)
            .build();
    }
}
