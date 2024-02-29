package ru.ioque.acceptance.application.tradingdatagenerator.stock;

import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGenerator;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;

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
